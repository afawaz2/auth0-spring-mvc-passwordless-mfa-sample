<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Home Page</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.css">
    <link rel="stylesheet" type="text/css" href="/css/jumbotron-narrow.css">
    <link rel="stylesheet" type="text/css" href="/css/jquery.growl.css"/>
    <script src="http://code.jquery.com/jquery.js"></script>
    <script src="https://cdn.auth0.com/js/auth0/8.9.3/auth0.min.js"></script>
    <script src="/js/jquery.growl.js" type="text/javascript"></script>
</head>

<body>

<script type="text/javascript">
    var webAuth = new auth0.WebAuth({
        clientID: '${clientId}',
        domain: '${domain}',
        redirectUri: "${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/acallback"
    });
</script>

<div class="container">
    <div class="header clearfix">
        <nav>
            <ul class="nav nav-pills pull-right">
                <li class="active" id="home"><a href="#">Home</a></li>
                <c:if test="${linkDropbox}">
                    <li id="link-dropbox"><a href="#">Dropbox Upgrade</a></li>
                </c:if>
                <li id="logout"><a href="#">Logout</a></li>
            </ul>
        </nav>
        <h3 class="text-muted">App.com</h3>
    </div>
    <div class="jumbotron">
        <h3>Hello ${user.name}!</h3>
        <p class="lead">Your nickname is: ${user.nickname}</p>
        <p class="lead">Your user id is: ${user.userId}</p>
        <c:if test="${linkDropbox}">
            <p class="lead">Dropbox email: ${dropboxEmail}</p>
        </c:if>
        <c:if test="${hasMfa == false}">
            <p><a id="mfa-btn" class="btn btn-lg btn-success" href="#" role="button">Sign up for MFA</a></p>
        </c:if>
        <p><img class="avatar" src="${user.picture}"/></p>
    </div>
</div>

<script type="text/javascript">

    <c:if test="${linkDropbox}">
        $('#link-dropbox').click(function () {
            $("#home").removeClass("active");
            $("#logout").removeClass("active");
            $("#link-dropbox").addClass("active");

            $.growl.notice({ message: "Linking dropbox." });
            setTimeout(function () {
                webAuth.authorize({
                    connection: 'dropbox',
                    scope: 'openid name email picture',
                    state: '${state}',
                    responseType: 'code'
                }, function (err) {
                    // this only gets called if there was an error
                    console.error('Error logging in: ' + err);
                });
            }, 3000);
        });
    </c:if>

    $('#mfa-btn').click(function () {
        console.log('Clicked!');
        $.growl.notice({ message: "MFA signup in progress. Require re-login." });
        setTimeout(function () {
            window.location = "${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/portal/mfa?mfaNonce=${mfaNonce}"
        }, 3000);
    });

    $("#logout").click(function(e) {
        e.preventDefault();
        $("#home").removeClass("active");
        $("#password-login").removeClass("active");
        $("#logout").addClass("active");
        // assumes we are not part of SSO so just logout of local session
        window.location = "${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}/logout";
    });

</script>

</body>
</html>