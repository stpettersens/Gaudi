/*
	Script functions for Gaudi project website.
	Copyright (c) 2011 Sam Saint-Pettersen.
	
	This script works in conjunction with:
	
	JQuery library 1.5+ - http://www.jquery.com
*/

//
// Function called on loading of page
//
function onLoad() {

	// Get browser user agent string
	var userAgentStr = navigator.userAgent;
	
	// In Apple WebKit-based browsers, add 2ems to description width.
	if(userAgentStr.search(/AppleWebKit/) != -1) {
		$('div#description').css('width', '42em');
	}

	// Make dependencies anchor a link and hide dependencies
	$('a#deps_link').attr('href', 'javascript:toggleDependencies();');
	$('a#dep_link').attr('href', 'javascript:jumpToDependencies();');
	$('div#deps_box').hide();
	
	// Add a message crediting jQuery with link to project website to footer.
	$('div#footer').append('<p>Enhanced with '
	+ '<a class="flink" href="http://jquery.com">jQuery</a> '
	+ 'and <a class="flink" href="http://github.com/stpettersens/ClipboardApplet">'
	+ 'ClipboardApplet</a>.</p><p><g:plusone></g:plusone>'
        + '<a class="FlattrButton" style="display:none;" rev="flattr;button:compact;"' 
        + ' href="http://stpettersens.github.com/Gaudi/"></a>'
        + '<noscript><a href="http://flattr.com/thing/345893/Gaudi-agnostic-build-tool-for-the-JVM" target="_blank">'
        + '<img src="http://api.flattr.com/button/flattr-badge-large.png" alt="Flattr this" title="Flattr this" border="0" />'           + '</a></noscript>');

	// Make all "extern" and "flink" class links open in new window (target="_blank").
	$('a.extern').attr('target', '_blank');
	$('a.flink').attr('target', '_blank');
}

//
// Function to show message when Java is not enabled
//
function showNoJava() {

	// Show message when Java is not enabled in the browser
	$('div#footer').prepend('<p><strong>Java is either unsupported or disabled in the browser,'
	+ '<br/>so ClipboardApplet won\'t run.'
	+ ' [<a class="flink" href="http://www.java.com/en/download/help/enable_browser.xml">'
	+ ' [<a class="flink" href="http://www.java.com/en/download/help/enable_browser.xml">'
	+ 'How do I fix this?</a>]</strong></p>');
}

//
// Function to show/hide box with dependencies for Gaudi application
//
function toggleDependencies() {

	// Fade in, fade out
	$('div#deps_box').fadeToggle('slow');
}
