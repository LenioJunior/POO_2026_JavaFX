#!/bin/bash

set -e

echo "========================================"
echo "CONFIGURANDO AMBIENTE JAVA FX"
echo "========================================"

BASE_DIR="$HOME/Downloads/Tools"
MAVEN_VERSION="3.9.16"
SCENE_BUILDER_VERSION="26.0.0"

mkdir -p "$BASE_DIR"

cd "$BASE_DIR"

# =========================
# MAVEN
# =========================

echo
echo "Baixando Maven..."

wget https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip

echo
echo "Extraindo Maven..."

unzip -o apache-maven-$MAVEN_VERSION-bin.zip

MAVEN_HOME="$BASE_DIR/apache-maven-$MAVEN_VERSION"

# =========================
# SCENE BUILDER
# =========================

echo
echo "Baixando Scene Builder..."

wget -O scenebuilder.deb https://download2.gluonhq.com/scenebuilder/$SCENE_BUILDER_VERSION/install/linux/SceneBuilder-$SCENE_BUILDER_VERSION.deb

echo
echo "Instalando Scene Builder..."

sudo dpkg -i scenebuilder.deb || sudo apt --fix-broken install -y

# =========================
# PATH
# =========================

echo
echo "Configurando PATH..."

echo "" >> ~/.bashrc
echo "# Maven" >> ~/.bashrc
echo "export MAVEN_HOME=$MAVEN_HOME" >> ~/.bashrc
echo 'export PATH=$MAVEN_HOME/bin:$PATH' >> ~/.bashrc

#source ~/.bashrc

echo
echo "========================================"
echo "INSTALACAO FINALIZADA"
echo "========================================"

echo
echo "Execute:"
echo "source ~/.bashrc"
