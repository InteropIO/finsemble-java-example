<html>
    <head>
        <script>
            function post_to_url(path, params) {
                for(var key in params) {
					if(path.includes("?"))
						path+="&"+key+"="+params[key]
					else
						path+="?"+key+"="+params[key]

                }

				var xhttp = new XMLHttpRequest();

				
				xhttp.open("POST", path, true);
				xhttp.send();
            }

            function spawnWelcome(){
                post_to_url("JavaServletExample",{action:'spawn',componentName:"Welcome Component"})
            }
			
			function getComponentList(){
				post_to_url("JavaServletExample",{action:'getComponentList'})
			}

        </script>
    </head>
    <body>
        <h1>Simple Java Web App</h1>
        <button onClick="spawnWelcome()">Spawn Welcome Component By Servlet</button> <br/>
    </body>
</html>
