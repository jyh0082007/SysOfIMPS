<?php if (!defined('THINK_PATH')) exit();?><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><head>
<title>照片墙    IMPSWEB 基于地理位置的SNS</title>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<link rel="stylesheet" type="text/css" href="/IMPS/Tpl/default/common/js/PolaroidPhotobarGallery/css/style.css" />
<script type="text/javascript" src="/IMPS/Tpl/default/common/js/PolaroidPhotobarGallery/js/jquery.min.js"></script>
<script type="text/javascript" src="/IMPS/Tpl/default/common/js/PolaroidPhotobarGallery/js/cufon-yui.js"></script>
<script type="text/javascript" src="/IMPS/Tpl/default/common/js/Public/PolaroidPhotobarGallery/js/ZHSRXT-GBK.cufon.js"></script>
<script type="text/javascript">
	Cufon.replace('span');
	Cufon.replace('h1', {
		textShadow: '0px 1px #ddd'
	});
</script>
</head>
<body>

		<h1><?php echo $_SESSION['username'].'的' ?>照片集</h1>
		<div id="pp_gallery" class="pp_gallery">
			
			<div id="pp_loading" class="pp_loading"></div>
			<div id="pp_next" class="pp_next"></div>
			<div id="pp_prev" class="pp_prev"></div>
			<div id="pp_thumbContainer">
<?php if(is_array($vararr)): $i = 0; $__LIST__ = $vararr;if( count($__LIST__)==0 ) : echo "" ;else: foreach($__LIST__ as $key=>$vo): ++$i;$mod = ($i % 2 )?><div class="album">
                <?php if(is_array($vo['photolist'])): $i = 0; $__LIST__ = $vo['photolist'];if( count($__LIST__)==0 ) : echo "" ;else: foreach($__LIST__ as $key=>$sub): ++$i;$mod = ($i % 2 )?><div class="content">
						<img src="/IMPS/Uploads/album/s_<?php echo ($sub["pickey"]); ?>" alt="/IMPS/Uploads/album/b_<?php echo ($sub["pickey"]); ?>" />
						<span><?php echo ($sub["name"]); ?></span>
					</div><?php endforeach; endif; else: echo "" ;endif; ?>
					<div class="descr">
						<?php echo ($vo["albumlist"]["album_name"]); ?>
					</div>
				</div><?php endforeach; endif; else: echo "" ;endif; ?>

				<div id="pp_back" class="pp_back">返回相册列表</div>
			</div>
		</div>
        <div>
            <span class="reference" style="padding-right:100px;">
              <a href="javascript:void(0);changebg();">更换背景</a>
            </span>
            <span class="reference">
                <a href="__APP__/album">返回</a>
            </span>
		</div>

        <!-- The JavaScript -->
<script type="text/javascript" src="/IMPS/Tpl/default/common/js/PolaroidPhotobarGallery/js/jquery.transform-0.8.0.min.js"></script>
        <script type="text/javascript">
           function changebg(){
        		   document.body.style.background='url("/IMPS/Tpl/default/common/js/PolaroidPhotobarGallery/bg2.jpg")';   
           }
            $(function() {
				var ie 			= false;
				if ($.browser.msie) {
					ie = true;
				}
				//current album/image displayed 
				var enableshow  = true;
				var current		= -1;
				var album		= -1;
				//windows width
				var w_width 	= $(window).width();
				//caching
				var $albums 	= $('#pp_thumbContainer div.album');
				var $loader		= $('#pp_loading');
				var $next		= $('#pp_next');
				var $prev		= $('#pp_prev');
				var $images		= $('#pp_thumbContainer div.content img');
				var $back		= $('#pp_back');
				
				//we wnat to spread the albums through the page equally
				//number of spaces to divide with:number of albums plus 1
				var nmb_albums	= $albums.length;
				var spaces 		= w_width/(nmb_albums+1);
				var cnt			= 0;
				//preload all the images (thumbs)
				var nmb_images	= $images.length;
				var loaded  	= 0;
				$images.each(function(i){
					var $image = $(this);
					$('<img />').load(function(){
						++loaded;
						if(loaded == nmb_images){
							//let's spread the albums equally on the bottom of the page
							$albums.each(function(){
								var $this 	= $(this);
								++cnt;
								var left	= spaces*cnt - $this.width()/2;
								$this.css('left',left+'px');
								$this.stop().animate({'bottom':'0px'},500);
							}).unbind('click').bind('click',spreadPictures);
							//also rotate each picture of an album with a random number of degrees
							$images.each(function(){
								var $this 	= $(this);
								var r 		= Math.floor(Math.random()*41)-20;
								$this.transform({'rotate'	: r + 'deg'});
							});
						}
					}).attr('src', $image.attr('src'));
				});
				
				function spreadPictures(){
					var $album 	= $(this);
					//track which album is opened
					album		= $album.index();
					//hide all the other albums
					$albums.not($album).stop().animate({'bottom':'-90px'},300);
					$album.unbind('click');
					//now move the current album to the left 
					//and at the same time spread its images through 
					//the window, rotating them randomly. Also hide the description of the album
					
					//store the current left for the reverse operation
					$album.data('left',$album.css('left'))
						  .stop()
						  .animate({'left':'0px'},500).find('.descr').stop().animate({'bottom':'-30px'},200);
					var total_pic 	= $album.find('.content').length;
					var cnt			= 0;
					//each picture
					$album.find('.content')
						  .each(function(){
						var $content = $(this);
						++cnt;
						//window width
						var w_width 	= $(window).width();
						var spaces 		= w_width/(total_pic+1);
						var left		= (spaces*cnt) - (140/2);
						var r 			= Math.floor(Math.random()*41)-20;
						//var r = Math.floor(Math.random()*81)-40;
						$content.stop().animate({'left':left+'px'},500,function(){
							$(this).unbind('click')
								   .bind('click',showImage)
								   .unbind('mouseenter')
								   .bind('mouseenter',upImage)
								   .unbind('mouseleave')
								   .bind('mouseleave',downImage);
						}).find('img')
						  .stop()
						  .animate({'rotate': r+'deg'},300);
						$back.stop().animate({'left':'0px'},300);
					});
				}
				
				//back to albums
				//the current album gets its innitial left position
				//all the other albums slide up
				//the current image slides out
				$back.bind('click',function(){
					$back.stop().animate({'left':'-100px'},300);
					hideNavigation();
					//there's a picture being displayed
					//lets slide the current one up
					if(current != -1){
						hideCurrentPicture();
					}
					
					var $current_album = $('#pp_thumbContainer div.album:nth-child('+parseInt(album+1)+')');
					$current_album.stop()
								  .animate({'left':$current_album.data('left')},500)
								  .find('.descr')
								  .stop()
								  .animate({'bottom':'0px'},500);
					
					$current_album.unbind('click')
								  .bind('click',spreadPictures);
					
					$current_album.find('.content')
							  .each(function(){
								var $content = $(this);
								$content.unbind('mouseenter mouseleave click');
								$content.stop().animate({'left':'0px'},500);
								});
								
					$albums.not($current_album).stop().animate({'bottom':'0px'},500);
				});
				
				//displays an image (clicked thumb) in the center of the page
				//if nav is passed, then displays the next / previous one of the 
				//current album
				function showImage(nav){
					if(!enableshow) return;
					enableshow = false;
					if(nav == 1){
						//reached the first one
						if(current==0){
							enableshow = true;
							return;
						}
						var $content 			= $('#pp_thumbContainer div.album:nth-child('+parseInt(album+1)+')')
												  .find('.content:nth-child('+parseInt(current)+')');
						//reached the last one
						if($content.length==0){
							enableshow = true;
							current-=2;
							return;
						}	
					}
					else
						var $content 			= $(this);
					
					//show ajax loading image
					$loader.show();
					
					//there's a picture being displayed
					//lets slide the current one up
					if(current != -1){
						hideCurrentPicture();
					}
					
					current 				= $content.index();
					var $thumb				= $content.find('img');
					var imgL_source 	 	= $thumb.attr('alt');
					var imgL_description 	= $thumb.next().html();
					//preload the large image to show
					$('<img style=""/>').load(function(){
						var $imgL 	= $(this);
						//resize the image based on the windows size
						resize($imgL);
						//create an element to include the large image
						//and its description
						var $preview = $('<div />',{
							'id'		: 'pp_preview',
							'className'	: 'pp_preview',
							'html'     	: '<div class="pp_descr"><span>'+imgL_description+'</span></div>',
							'style'		: 'visibility:hidden;'
						});
						$preview.prepend($imgL);
						$('#pp_gallery').prepend($preview);
						
						var largeW 				= $imgL.width()+20;
						var largeH 				= $imgL.height()+10+45;
						//change the properties of the wrapping div 
						//to fit the large image sizes
						$preview.css({
							'width'			:largeW+'px',
							'height'		:largeH+'px',
							'marginTop'		:-largeH/2-20+'px',
							'marginLeft'	:-largeW/2+'px',
							'visibility'	:'visible'
						});
						Cufon.replace('.pp_descr');
						//show navigation
						showNavigation();
						
						//hide the ajax image loading
						$loader.hide();
						
						//slide up (also rotating) the large image
						var r 			= Math.floor(Math.random()*41)-20;
						if(ie)
							var param = {
								'top':'50%'
							};
						else
							var param = {
								'top':'50%',
								'rotate': r+'deg'
							};
						$preview.stop().animate(param,500,function(){
							enableshow = true;
						});
					}).error(function(){
						//error loading image. Maybe show a message : 'no preview available'?
					}).attr('src',imgL_source);	
				}
				
				//click next image
				$next.bind('click',function(){
					current+=2;
					showImage(1);
				});
				
				//click previous image
				$prev.bind('click',function(){
					showImage(1);
				});
				
				//slides up the current picture
				function hideCurrentPicture(){
					current = -1;
					var r 	= Math.floor(Math.random()*41)-20;
					if(ie)
						var param = {
							'top':'-150%'
						};
					else
						var param = {
							'top':'-150%',
							'rotate': r+'deg'
						};
					$('#pp_preview').stop()
									.animate(param,500,function(){
										$(this).remove();
									});
				}
				
				//shows the navigation buttons
				function showNavigation(){
					$next.stop().animate({'right':'0px'},100);
					$prev.stop().animate({'left':'0px'},100);
				}
				
				//hides the navigation buttons
				function hideNavigation(){
					$next.stop().animate({'right':'-40px'},300);
					$prev.stop().animate({'left':'-40px'},300);
				}
				
				//mouseenter event on each thumb
				function upImage(){
					var $content 	= $(this);
					$content.stop().animate({
						'marginTop'		: '-70px'
					},400).find('img')
						  .stop()
						  .animate({'rotate': '0deg'},400);
				}
				
				//mouseleave event on each thumb
				function downImage(){
					var $content 	= $(this);
					var r 			= Math.floor(Math.random()*41)-20;
					$content.stop().animate({
						'marginTop'		: '0px'
					},400).find('img').stop().animate({'rotate': r + 'deg'},400);
				}
				
				//resize function based on windows size
				function resize($image){
					var widthMargin		= 50
					var heightMargin 	= 200;
					
					var windowH      = $(window).height()-heightMargin;
					var windowW      = $(window).width()-widthMargin;
					var theImage     = new Image();
					theImage.src     = $image.attr("src");
					var imgwidth     = theImage.width;
					var imgheight    = theImage.height;

					if((imgwidth > windowW)||(imgheight > windowH)){
						if(imgwidth > imgheight){
							var newwidth = windowW;
							var ratio = imgwidth / windowW;
							var newheight = imgheight / ratio;
							theImage.height = newheight;
							theImage.width= newwidth;
							if(newheight>windowH){
								var newnewheight = windowH;
								var newratio = newheight/windowH;
								var newnewwidth =newwidth/newratio;
								theImage.width = newnewwidth;
								theImage.height= newnewheight;
							}
						}
						else{
							var newheight = windowH;
							var ratio = imgheight / windowH;
							var newwidth = imgwidth / ratio;
							theImage.height = newheight;
							theImage.width= newwidth;
							if(newwidth>windowW){
								var newnewwidth = windowW;
								var newratio = newwidth/windowW;
								var newnewheight =newheight/newratio;
								theImage.height = newnewheight;
								theImage.width= newnewwidth;
							}
						}
					}
					$image.css({'width':theImage.width+'px','height':theImage.height+'px'});
				}
            });
        </script>


</body>
</html>