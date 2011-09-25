/*
    Break out of an iframe.
*/
function breakoutFrame() {
    if(top.location != location) {
        top.location.href = document.location.href;
    }
}
breakoutFrame();
