<!DOCTYPE html>
<html>
  <head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1" />
    <title>commerz_poc_html</title>
    <meta name="description" content="" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <link rel="stylesheet" type="text/css" href="index.css" />
    <script src="index.js"></script>
  </head>
  <body>
    <h1>commerz_poc_jsp</h1>
    <div
      style="width:300px;height:200px;background-color: blue;"
      id="color"
      class="center"
    ></div>
    <div class="center">
        <button onClick="spawnSwing()">Spawn Swing By Servlet</button>
        <button onClick="spawnHtml()">Spawn HTML By Servlet</button>
        <button onClick="FSBL.Clients.LauncherClient.spawn('commerz_poc_swing', { 'addToWorkspace': true })">Spawn Swing By JavaScript</button>
        <button onClick="FSBL.Clients.LauncherClient.spawn('commerz_poc_html', { 'addToWorkspace': true })">Spawn HTML By JavaScript</button>
    </div>
  </body>
</html>
