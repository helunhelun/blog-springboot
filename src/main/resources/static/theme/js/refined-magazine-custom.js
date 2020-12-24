jQuery(document).ready(function ($) {
    if ($('#loader-wrapper').length > 0) {
        // hide preloader when everthing in the document load
        $('#loader-wrapper').css('display', 'none');
    }

    //Open Search form on search icon click
    if ($('.search-icon-box').length > 0) {
        $('.search-icon-box').on('click', function (e) {
            e.preventDefault();
            refined_magazine_search();

        });
    }

    function refined_magazine_search(e) {
        $('.top-bar-search').addClass('open');
        $('.top-bar-search form input[type="search"]').focus();
        var focusableEls = $('.top-bar-search a[href]:not([disabled]), .top-bar-search button:not([disabled]), .top-bar-search input:not([disabled])');
        var firstFocusableEl = focusableEls[0];
        var lastFocusableEl = focusableEls[focusableEls.length - 1];
        var KEYCODE_TAB = 9;
        $('.top-bar-search').on('keydown', function(e) {
            if (e.key === 'Tab' || e.keyCode === KEYCODE_TAB) {
                if ( e.shiftKey ) /* shift + tab */ {
                    if (document.activeElement === firstFocusableEl) {
                        lastFocusableEl.focus();
                        e.preventDefault();
                    }
                } else /* tab */ {
                    if (document.activeElement === lastFocusableEl) {
                        firstFocusableEl.focus();
                        e.preventDefault();
                    }
                }
            }
        });
    }

    // Close popup search form
    $('.top-bar-search, .top-bar-search .close').on('click', function (event) {
        if (event.target == this || event.target.className == 'close' || event.keyCode == 27) {

            $('.refined-magazine-featured-block').show();
            $('.refined-magazine-post-slider').show();
            $('.slick-slider').slick('reinit');
            $('.top-bar-search').removeClass('open');
            $('.search-icon-box').focus();
            $('input, a, button, select').each(function () {
                $(this).attr('tabindex', '0');
                $(this).attr('tabindex', '0');
            });
        }
    });

    //Post Slider Widget JS
    if ($('.ct-post-carousel').length > 0) {
        $(".ct-post-carousel").slick({
            items: 1,
            dots: false,
            infinite: true,
            centerMode: false,
            autoplay: true,
            lazyLoad: 'ondemand',
            adaptiveHeight: true
        });
    }

    //Tabs
    if ($('.ct-tabs').length > 0) {
        $(".ct-tabs").tabs();
    }
    // Initialize gototop button
    if ($('#toTop').length > 0) {
        // Hide the toTop button when the page loads.
        $("#toTop").css("display", "none");

        // This function runs every time the user scrolls the page.
        $(window).scroll(function () {

            // Check weather the user has scrolled down (if "scrollTop()"" is more than 0)
            if ($(window).scrollTop() > 0) {

                // If it's more than or equal to 0, show the toTop button.
                $("#toTop").fadeIn("slow");
            } else {
                // If it's less than 0 (at the top), hide the toTop button.
                $("#toTop").fadeOut("slow");

            }
        });

        // When the user clicks the toTop button, we want the page to scroll to the top.
        jQuery("#toTop").click(function (event) {

            // Disable the default behaviour when a user clicks an empty anchor link.
            // (The page jumps to the top instead of // animating)
            event.preventDefault();

            // Animate the scrolling motion.
            jQuery("html, body").animate({
                scrollTop: 0
            }, "slow");

        });
    }


    //sticky sidebar
    var at_body = $("body");
    var at_window = $(window);

    if ($('.ct-sticky-sidebar').length > 0) {

        if (at_body.hasClass('ct-sticky-sidebar')) {
            if (at_body.hasClass('right-sidebar')) {
                $('#secondary, #primary').theiaStickySidebar();
            } else {
                $('#secondary, #primary').theiaStickySidebar();
            }
        }
    }

    //Trending News Marquee
    if ($('.trending-left').length > 0) {
        $('.trending-left').marquee({
            //speed in milliseconds of the marquee
            duration: 85000,
            //gap in pixels between the tickers
            gap: 0,
            //time in milliseconds before the marquee will start animating
            delayBeforeStart: 0,
            //'left' or 'right'
            direction: 'left',
            //true or false - should the marquee be duplicated to show an effect of continues flow
            duplicated: true,

            pauseOnHover: true,
            startVisible: true
        });
    }

    //Trending News Marquee
    if ($('.trending-right').length > 0) {
        $('.trending-right').marquee({
            //speed in milliseconds of the marquee
            duration: 85000,
            //gap in pixels between the tickers
            gap: 0,
            //time in milliseconds before the marquee will start animating
            delayBeforeStart: 0,
            //'left' or 'right'
            direction: 'right',
            //true or false - should the marquee be duplicated to show an effect of continues flow
            duplicated: true,

            pauseOnHover: true,
            startVisible: true
        });
    }

    if (jQuery('.sticky-header').length > 0) {
        // grab the initial top offset of the navigation
        var stickyNavTop = $('.sticky-header').offset().top;


        // our function that decides weather the navigation bar should have "fixed" css position or not.
        var stickyNav = function () {
            var width = $(window).width();
            var scrollTop = $(window).scrollTop(); // our current vertical position from the top

            // if we've scrolled more than the navigation, change its position to fixed to stick to top,
            // otherwise change it back to relative
            if ((scrollTop > stickyNavTop) && (width > 768)) {
                $('.sticky-header').addClass('ct-sticky');
                $('.sticky-header').removeClass('hide');
            } else {
                $('.sticky-header').removeClass('ct-sticky');
                $('.sticky-header').addClass('hide');
            }
        };

        stickyNav();
        // and run it again every time you scroll
        $(window).scroll(function () {
            stickyNav();
        });
    }

    if (jQuery('.ct-show-hide-top').length > 0) {
        $('.ct-show-hide-top').on('click', function (e) {
            e.preventDefault();
            $('.top-bar .container-inner').toggle('slow');
            $(this).toggleClass('ct-rotate');
        });
    }

    $('.menu-toggle').on('click', function () {
        if ($('.main-navigation').hasClass('toggled')) {

            var focusableEls = $('#primary-menu a[href]:not([disabled])');
            var lastFocusableEl = focusableEls[focusableEls.length - 1];
            $('#primary-menu').on('keydown', function (e) {
                    if (e.shiftKey) /* shift + tab */ {

                    }else{
                        if (document.activeElement === lastFocusableEl) {
                            $('.menu-toggle').focus();
                            e.preventDefault();
                        }
                    }
            });


        }
    });
});