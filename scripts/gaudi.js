/*
	Script functions for Gaudi project website.
	Copyright (c) 2011 Sam Saint-Pettersen.
	
	This script works in conjunction with:
	
	JQuery library 1.5+ - http://www.jquery.com
*/

//
// Function called on loading of page.
//
function onLoad() {

	// Get browser user agent string
	var userAgentStr = navigator.userAgent;
	
	// In Apple WebKit-based browsers, add 2ems to description width.
	if(userAgentStr.search(/AppleWebKit/) != -1) {
		$('div#description').css('width', '42em');
	}

	// Make dependencies anchor a link and hide dependencies.
	$('a#deps_link').attr('href', 'javascript:toggleDependencies();');
	$('a#dep_link').attr('href', 'javascript:jumpToDependencies();');
	$('div#deps_box').hide();


	// Add extra material to page footer.
	$('div#footer').append('<div><p>Enhanced with '
	+ '<a class="flink" href="http://jquery.com">jQuery</a>, '
	+ '<a class="flink" href="http://headjs.com">headJS</a> '
	+ 'and <a class="flink" href="http://github.com/stpettersens/ClipboardApplet">'
	+ 'ClipboardApplet</a>.</p><p><span style="padding-left: 30%;"><g:plusone></g:plusone>'
	+ '</p></div>');

	// Make all "extern" and "flink" class links open in new window (target="_blank").
	$('a.extern').attr('target', '_blank');
	$('a.flink').attr('target', '_blank');
}

//
// Function to show/hide box with dependencies for Gaudi application.
//
function toggleDependencies() {

	// Fade in, fade out
	$('div#deps_box').fadeToggle('slow');
}
