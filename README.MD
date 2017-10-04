# dattach

Util for connecting to mail server and downloading the attached files.
For detailed processing, you can use java RegEx for senders, subject, files.

### How to use
usage: java -jar dattach.jar [OPTION]

 -a,--mask-address <RegExpr>      Java regular expression pattern for filtering sender email address. This mails will be processed.
 
 -c,--catalog <path to catalog>   Path to the directory into which the files will be saved.
 
 -f,--mask-files <RegExpr>        Java regular expression pattern for filtering attached files. This files will be downloaded.
 
 -h,--help                        print this message
 
 -l,--login <mail login>          Login with which you access your e-mail
 
 -p,--password <password>         Password for accessing the mailbox
 
 -r,--remove                      Delete emails that have been processed
 
 -s,--server <imap server>        Imap mail server where attachments will  be downloaded (example: imap.name.org)
    
 --sec                            Use security connection to mail server on imap protocol 993
 
 -t,--mask-topic <RegExpr>        Java regular expression pattern for filtering email topic. This mails will be processed
 
 -v,--verbose                     Verbose info about processing
 
#### Example: 
`java -jar dattach.jar -simap.mail.server -luser@server.org -pPwd -c/home/user/Desktop`

Process finished with exit code 0
