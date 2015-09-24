function toggleTOC(){
	$(".nav-menu").toggleClass('hidden_small');
}

$( document ).ready(function() {
   $(".nav-menu li").click(function(){
   	toggleTOC();
   });


/*change the background for <pre> blocks that contains <code> in order to fit with the monokai theme */
$("pre code").parent().css("background-color", "#23241F");


/* This displays or hides elementos of the TOC */
$('#toc li a').click(function()
{
	$(this).parent().parent().children("li").removeClass("extended_menu");
	$(this).parent().addClass("extended_menu");
});

});	


