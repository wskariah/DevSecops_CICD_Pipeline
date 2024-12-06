FROM jenkins/jenkins:lts

# Install necessary dependencies for Podman
USER root
RUN apt-get update && \
    apt-get install -y podman curl && \
    apt-get clean

# Switch back to Jenkins user
USER jenkins
