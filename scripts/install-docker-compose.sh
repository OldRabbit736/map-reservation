# Amazon Linux 2023에서 Docker Compose 설치

# 시스템 패키지를 최신 상태로 업데이트한다.
sudo dnf update -y
# DNF를 사용하여 Docker를 설치한다.
sudo dnf install docker -y
# Docker 서비스를 시작하고 부팅 시 자동으로 실행되도록 설정한다.
sudo systemctl start docker
sudo systemctl enable docker
# 최신 버전의 Docker Compose를 설치한다.
sudo curl -L "https://github.com/docker/compose/releases/download/v2.6.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
# 다운로드한 Docker Compose 바이너리에 실행 권한을 부여한다.
sudo chmod +x /usr/local/bin/docker-compose
docker-compose --version
