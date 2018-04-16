$(document).ready(function() {		
		
/*		$('img').load(function(){
			var w = $(this).width();
			var h = $(this).height();
			console.log("h="+h+"w="+w);
			if(w *3 > 1000)
			{
				w= 1000/3;
			}
			else
			{
				w= w/3;
				h = h/3;
			}
			$(this).css({
				width:w,
				height:h,
			});

		});
*/		
	
        /*$("img").each(function(){
            var w = $(this).width();
			var h = $(this).height();
			console.log("11 h="+h+"w="+w);
			if(w *3 > 1000)
			{
				w= 1000/3;
			}
			else
			{
				w= w/3;
				h = h/3;
			}
			console.log("22 h="+h+"w="+w);
			$(this).css({
				width:w,
				height:h,
			});
        })*/

		$('[button_type="self-rating"]').on("click", function() {
            var id = $(this).attr("id");
			if(id)
			{
				JavaScriptObject.setSelfRating(id);
			}
        });
		
		$('input').attr("disabled","disabled");
		$('div').removeAttr("contenteditable");
        
	})