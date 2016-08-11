# site-mailer
The simple sender е-mail from a site

#### Usage:
```bash
java -jar mailer.jar mailer.properties
```

#### mailer.properties
```ini
#service path handler [REQUIRED]
service.handler=/mailer
#service port [REQUIRED]
service.port=50465

#e-mail address of the recipient of messages  [REQUIRED]
email.to=enhorse.xyz@gmail.com
#email address from which will come emails [by default = email.to]
email.from=mailer@enhorse.xyz
#e-mail address of the recipient of error messages [by default = email.to]
email.admin=pavel13kalinin@gmail.com

#SMTP server address [REQUIRED]
smtp.host=aspmx.l.google.com
#SMTP port [by default = 465, if smtp.tls = true or smtp.ssl = true; otherwise = 25]
smtp.port=25
#SMTP server requires authentication [by default = false]
smtp.auth=true
#SMTP server requires SSL [by default = false]
smtp.ssl=false
#SMTP server requires TLS [by default = false]
smtp.tls=false
#username for SMTP server[by default = ""]
smtp.user=username
#password for SMTP server[by default = ""]
smtp.password=password
#value of the RFC's header "X-Mailer" [by default = ""]
smtp.x-mailer=site-mailer for enhorse.xyz

#turn on/off debug mode for the service [by default = false]
service.debug=false
#turn on/off debug mode for the SMTP server [by default = false]
smtp.debug=false
#turn on/off debug for Jetty HTTP server [by default = false]
jetty.debug=false
```

#### HTML Form
```html
<form method="post" action="/mailer">   
    <input type="text" name="name" placeholder="Name"/>
    <input type="text" name="email" placeholder="Email"/>
    <input type="text" name="subject" placeholder="Subject"/>
    <textarea name="content" placeholder="Message" rows="8"></textarea>
    
    <input type="submit" value="Send"/>
    
    <!--If a message was sent successfully-->
    <input type="hidden" name="success" value="/success.html">
    <!--If send a message fails-->
    <input type="hidden" name="fail" value="/fail.html">
</form>
```