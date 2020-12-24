function jsLiveCheckUpdts(){
        var match_id = jQuery("#match_id").val();
        var data = {
        'action': 'livematch_score',
        'match_id' : match_id,
        };
        
        
        jQuery.post(ajaxurl, data, function(res) {
            var IS_JSON = true;
            
            try
            {
                var json = JSON.parse(res);
            }
            catch(err)
            {
                IS_JSON = false;
            } 
            if(IS_JSON){
                
                //if(json.error == '0'){
                    //var jshtml = JSON.parse(json.html);
                    //console.log(jshtml);
                    
                    if(json.score){
                        jQuery(".BigMScore1").html(json.score[0]);
                        jQuery(".BigMScore2").html(json.score[1]);
                        
                        var divMobile = jQuery(".jsMatchDivMobile").find(".jsScoreDiv");
                        
                        var scoreIs = divMobile.find("a");
                        scoreIs.html(json.score[0] + ' - ' + json.score[1]);
                    }
                    if(json.plstat){
                        jQuery("#jsPlayerStatMatchDiv").html(json.plstat);
                    }
                    if(json.plsquad){
                        jQuery("#stab_squad").html(json.plsquad);
                    }

                    
                    
                    var d_jsLUP = new Date();
                    jslive_last_upd = d_jsLUP.toUTCString();
                //}
            }
        });    
    }
    