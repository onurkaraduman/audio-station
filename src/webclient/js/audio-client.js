var websocket;

var parts = new Array();

window.setInterval(function playNext() {
    if (parts[0]) {
        play(parts[0].data);
        parts.splice(0, 1);
    }
}, 100);

function sendData() {
    var txt = document.getElementById("txtUrl");
    var url = txt.value;
    console.log(url);
    websocket.send(url);
}

function connectWebsocket() {
    if (window.WebSocket) {
        websocket = new WebSocket("ws://localhost:8081/stream");
        websocket.binaryType = "arraybuffer";

        websocket.onmessage = function (event) {

            var dataView = new DataView(event.data);
            var idx = dataView.getUint32(0);

            var data = event.data.slice(4); // first 4 bytes are the position
            var part = {
                idx: idx,
                data: data
            };
            console.log(part);
            parts.push(part);
            parts.sort(function (lhs, rhs) {
                return (lhs.idx < rhs.idx) ? -1
                    : ((lhs.idx == rhs.idx) ? 0 : 1);
            });
        };

    }
}

var AudioContext = window.AudioContext || window.webkitAudioContext;
var context = new AudioContext();

var sync = 0;

// Play the audio.
function play(audio) {
    context.decodeAudioData(audio, function (data) {
        var source = context.createBufferSource();
        source.buffer = data;
        source.connect(context.destination);
        sync = sync < context.currentTime ? context.currentTime : sync;
        console.log("Current sync : " + sync);
        source.start(sync);
        sync += source.buffer.duration;
        console.log("Next sync : " + sync);
    }, function (e) {
        console.log(e);
    });
}

var _appendBuffer = function (buffer1, buffer2) {
    var tmp = new Uint8Array(buffer1.byteLength + buffer2.byteLength);
    tmp.set(new Uint8Array(buffer1), 0);
    tmp.set(new Uint8Array(buffer2), buffer1.byteLength);
    return tmp.buffer;
};