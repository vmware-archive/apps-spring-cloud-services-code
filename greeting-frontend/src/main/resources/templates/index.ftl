<!doctype html>
<html>
<head>
    <title>Greetings!</title>
    <style>
        body {
            font-family: Georgia, serif;
            color: #555;
            background-color: #eff;
            letter-spacing: 1.2px;
            line-height: 1.4;
        }

        h1 {
            text-align: center;
            width: 60%;
            max-width: 800px;
            margin: 3em auto;
            padding: 2em;

            background-color: #fff;
            border-radius: .5em;
            box-shadow: 2px 2px 5px #888;
        }
    </style>
</head>
<body>

<script src="app.js"></script>
<script>Elm.GreetingApp.fullscreen({"apiServerUrl":"${apiServerUrl}"})</script>

</body>
</html>
