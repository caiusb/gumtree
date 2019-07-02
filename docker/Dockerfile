FROM ubuntu:disco

# installs all required packages
RUN apt-get update \
	&& apt-get install -y software-properties-common \
    && apt-get install -y wget locales \
	&& apt-get install -y openjdk-8-jdk libarchive13 curl ocaml=4.05.0-10ubuntu2 ocaml-native-compilers camlp4 git build-essential zip python-pip\
	&& wget http://131.123.42.38/lmcrs/beta/srcML-Ubuntu14.04-64.deb \
	&& dpkg -i srcML-Ubuntu14.04-64.deb \
	&& pip install jsontree asttokens

# Set the locale
RUN sed -i -e 's/# en_US.UTF-8 UTF-8/en_US.UTF-8 UTF-8/' /etc/locale.gen && \
    locale-gen
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

# install cgum
WORKDIR /opt
RUN git clone https://github.com/GumTreeDiff/cgum.git --depth 1
WORKDIR /opt/cgum
RUN make \
	&& ln -sv /opt/cgum/cgum /usr/bin/cgum

# install pythonparser
WORKDIR /opt
RUN git clone https://github.com/GumTreeDiff/pythonparser.git --depth 1
WORKDIR /opt/pythonparser
RUN ln -sv /opt/pythonparser/pythonparser /usr/bin/pythonparser

# install gumtree
WORKDIR /opt
RUN git clone -b master https://github.com/caiusb/gumtree.git --depth 1
WORKDIR /opt/gumtree
RUN ./gradlew build -x check \
	&& unzip dist/build/distributions/gumtree-2.1.2.zip \
	&& ln -sv /opt/gumtree/gumtree-2.1.2/bin/gumtree /usr/bin/gumtree \
	&& echo $PATH

# define volume diff to make available files to diff
RUN mkdir /diff
WORKDIR /diff
VOLUME /diff

# expose port 4567 for webdiff
EXPOSE 4567

ENTRYPOINT ["gumtree"]
