/*
    Break out of the gaudi-build.tk frame.
*/
function breakoutFrame() {
    if(top.location != location) {
        top.location.href = document.location.href;
    }
}
breakoutFrame();
