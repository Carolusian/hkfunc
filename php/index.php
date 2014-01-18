  <!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
  <html xmlns="http://www.w3.org/1999/xhtml">
    <head>
      <title>Comet demo</title>
      <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
      <link rel="stylesheet" media="screen" href="js/main.css">
      <script src="js/jquery-1.7.1.min.js" type="text/javascript"></script>
    </head>
    <body>
      <h1>Server Sent Event clock</h1>
    
      <h1 id="content"></h1>
        
      <script type="text/javascript">
        var evtSource = new EventSource("backend.php");
        evtSource.onmessage = function(e) {
            var elem = document.getElementById("content")
            elem.innerHTML = "Time: " + e.data;
        }
      </script>
  </body>
  </html>
  </html>