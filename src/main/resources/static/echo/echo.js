require({
        baseUrl: "../js/jquery",
        paths: {
            jquery: "https://code.jquery.com/jquery-3.2.1",
            cometd: "../cometd"
        }
    },
    [ "jquery", "jquery.cometd", "jquery.cometd-timestamp", 
    	"jquery.cometd-reload"/*, "jquery.cometd-ack"*/ ],

    function($, cometd) {
        $(function() {
            function echoRpc(text) {
                console.debug("Echoing", text);

                cometd.remoteCall("echo", {msg: text}, function(reply) {
                    var responses = $("#responses");
                    responses.html(responses.html() +
                        (reply.timestamp || "") + " Echoed by server: " + reply.data.msg + "<br/>");
                });
            }
            
            function publish() {
            	var phrase = $("#phrase");
            	var value = phrase.val();
            	phrase.val("");
            	
            	// Publish to a channel
    	    	cometd.publish('/echo', { msg: value } );
            }

            $(window).on("beforeunload", cometd.reload);

            var phrase = $("#phrase");
            phrase.attr("autocomplete", "OFF");
            phrase.on("keyup", function(e) {
                if (e.keyCode == 13) {
                    echoRpc(phrase.val());
                    phrase.val("");
                    return false;
                }
                return true;
            });
            var sendB = $("#sendB");
            sendB.on("click", function() {
                echoRpc(phrase.val());
                phrase.val("");
                return false;
            });
            
            var sendBroadcast = $("#broadcast");
            sendB.on("click", function() {
            	publish();
                return false;
            });

			// COMETD
            cometd.configure({
                url: "http://localhost:8080/cometd",
                logLevel: "debug"
            });

            // Meta canais sao definidos por padrao pela implementação do CometD
            cometd.addListener("/meta/handshake", function(reply) {
                if (reply.successful) {
                    echoRpc("Type something in the textbox above");
                } else {
                	console.error(" -> addListener");
                }
            });
            cometd.handshake();
            
            // Canais de serviços são utilizados para requisição e resposta entre cliente e servidor
            // SUBSCRIVER
            cometd.subscribe('/echo', function(obj) {
            	console.log("Subscrition: /echo: " + JSON.stringify(obj));
            });
        });
    });
