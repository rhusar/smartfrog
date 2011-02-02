When the project is built (using 'ant war' or just 'ant') a 'build' directory will be
created. Inside this directory will be the full contents of the compiled web-app.

The .java files from within 'src' will be compiled into 'build/WEB-INF/classes'
The .jar files from 'WEB-INF/lib' will be copied into 'build/WEB-INF/lib'
All files from 'static' will be copied into 'build' (image, html, jsp etc...)