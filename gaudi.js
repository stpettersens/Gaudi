/*
	Script functions for Gaudi project website.
	Copyright (c) 2011 Sam Saint-Pettersen.
	
	This script works in conjunction with:
	
	JQuery library 1.5+ - http://www.jquery.com
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

	// Make all "extern" and "flink" class links open in new window (target="_blank").
	$('a.extern').attr('target', '_blank');
	$('a.flink').attr('target', '_blank');
}

function showNoJava() {

	// Show message when Java is not enabled in the browser
	$('div#footer').prepend('<p><strong>Java is either unsupported or disabled in the browser,'
		+ '<br/>so ClipboardApplet won\'t run.'
		+ ' [<a class="flink" href="http://www.java.com/en/download/help/enable_browser.xml">'
		+ ' [<a class="flink" href="http://www.java.com/en/download/help/enable_browser.xml">'
		+ 'How do I fix this?</a>]</strong></p>');
}
