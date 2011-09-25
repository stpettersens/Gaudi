function breakOutFrame {
    if(top.location != location) {
        top.location.href = document.location.href;
    }
}
breakoutFrame();
