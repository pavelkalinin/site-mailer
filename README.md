# site-mailer
The simple sender е-mail from a site

#### usage:
```bash
java -jar mail.jar mailer.properties
```

#### mailer.properties
```ini
#service path handler [REQUIRED]
service.handler=/mailer
#service port [REQUIRED]
service.port=50465

#email address of recipient of messages  [REQUIRED]
email.to=enhorse.xyz@gmail.com
#email address from which emails are coming [by default = email.to]
email.from=mailer@enhorse.xyz
#email address of recipient of error messages [by default = email.to]
email.admin=pavel13kalinin@gmail.com

#SMTP server address [REQUIRED]
smtp.host=aspmx.l.google.com
#SMTP port [by default = 25 or 465, if smtp.tls = true or smtp.ssl = true]
smtp.port=25
#SMTP server requires authentication [by default = false]
smtp.auth=true
#SMTP server user [by default = ""]
smtp.user=username
#SMTP server password [by default = ""]
smtp.password=password
#SMTP server requires SSL [by default = false]
smtp.ssl=false
#SMTP server requires TLS [by default = false]
smtp.tls=false
#value for the RFC's header "X-Mailer" [by default = ""]
smtp.x-mailer=site-mailer for enhorse.xyz

#turn on/off debug mode for the application [by default = false]
service.debug=false
#turn on/off debug mode for javax.mail [by default = false]
smtp.debug=false
#turn on/off debug for jetty embedded server [by default = false]
jetty.debug=false
```

#### HTML Form
```html
<form method="post" action="/mailer">
    <!--if message was successfully sent redirect to -->
    <input type="hidden" name="success" value="/success.html">
    <!--if message sending was failed redirect to -->
    <input type="hidden" name="fail" value="/fail.html">
    <input type="text" name="name" placeholder="Name / Имя"/>
    <input type="text" name="email" placeholder="Email"/>
    <input type="text" name="subject" placeholder="Subject / Тема"/>
    <textarea name="content" placeholder="Message / Сообщение" rows="8"></textarea>
    <input type="submit" value="Send / Отправить"/>
</form>
```