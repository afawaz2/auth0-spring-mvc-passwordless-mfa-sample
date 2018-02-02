<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title>Login</title>
    <link rel="stylesheet" type="text/css" href="/css/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/css/jquery.growl.css"/>
    <script src="http://code.jquery.com/jquery.js"></script>
    <script src="https://cdn.auth0.com/js/lock/11.2/lock.min.js"></script>
    <script src="/js/jquery.growl.js" type="text/javascript"></script>
</head>
<body>

<div class="container">

    <script type="text/javascript">

        $(function () {
            var error = ${error};
            if (error) {
                $.growl.error({message: "An error was detected. Please log in"});
            } else {
                $.growl({title: "Welcome!", message: "Please log in"});
            }
        });

        $(function () {
            setTimeout(function () {
                var lock = new Auth0LockPasswordless('${clientId}', '${domain}', {
                    oidcConformant: true,
                    passwordlessMethod: 'link',
                    auth: {
                        responseType: 'code',
                        redirectUrl: '${fn:replace(pageContext.request.requestURL, pageContext.request.requestURI, '')}' + '/callback',
                        params: {
                            state: '${state}',
                            scope: 'openid profile'
                        }
                    }
                });
                lock.show();
            }, 2000);
        });

    </script>

</div>
</body>
</html>
