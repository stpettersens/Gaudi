/*
	Script functions for Gaudi project website
*/
function onLoad() {

	// Get browser user agent string
	var userAgentStr = navigator.userAgent;
	
	// In Apple WebKit-based browsers, add 2ems to description width.
	if(userAgentStr.search(/AppleWebKit/) != -1) {
		$('div#description').css('width', '42em');
	}
	
	// Add a message crediting jQuery with link to project website to footer.
	$('div#footer').append('<p>This website has been enhanced with '
	+ '<a class="flink" href="http://jquery.com">jQuery</a>.</p>');	
	
//	// Insert the ClipboardApplet for the Git repository checkout CLI command.
//	// Only do this if browser has Java enabled.
//	if(navigator.javaEnabled()) {
//		// Use <APPLET> tag in these browsers (Chrome includes "Safari" in its UA string).
//		if(userAgentStr.search(/Safari|Netscape|Firefox|Opera/) != -1) {
//			$('span#cbapplet').replaceWith(
//			'<applet code="ClipboardApplet.class" width="200" height="20"/>');
//		}
//		// Otherwise, use <EMBED> tag to insert the applet.
//		// TODO?
//	}
//	// Show message when Java is not enabled in the browser
//	else {
//		$('div#footer').prepend('<p><strong>Java is either unsupported or disabled in the browser,'
//		+ '<br/>so ClipboardApplet won\'t run.'
//		+ ' [<a class="flink" href="http://www.java.com/en/download/help/enable_browser.xml">'
//		+ 'How do I fix this?</a>]</strong></p>');
//	}
	
	// Make all "extern" and "flink" class links open in new window (target="_blank").
	$('a.extern').attr('target', '_blank');
	$('a.flink').attr('target', '_blank');
}
