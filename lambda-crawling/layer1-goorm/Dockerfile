FROM amazon/aws-lambda-python:3.12

# install chrome dependencies
RUN dnf install -y atk cups-libs gtk3 libXcomposite alsa-lib \
    libXcursor libXdamage libXext libXi libXrandr libXScrnSaver \
    libXtst pango at-spi2-atk libXt xorg-x11-server-Xvfb \
    xorg-x11-xauth dbus-glib dbus-glib-devel nss mesa-libgbm jq unzip


COPY ./chrome-installer.sh ./chrome-installer.sh
RUN ./chrome-installer.sh
RUN rm ./chrome-installer.sh

RUN pip install selenium
RUN pip install openai pymysql beautifulsoup4

COPY main.py ./
COPY mariadb_connection.py ./

CMD [ "main.lambda_handler" ]