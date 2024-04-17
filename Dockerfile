FROM maven:3.9.6-amazoncorretto-17 as log-filters-extension
WORKDIR /home/extension
COPY log-filters-extension/ .
RUN mvn clean install

FROM quay.io/keycloak/keycloak:latest as builder
ARG LOG_FILTERS_EXTENSION_VERSION="1.0.0-SNAPSHOT"
ARG LOG_FILTERS_EXTENSION_RUNTIME_JAR_PATH=/home/extension/runtime/target/log-filters-extension-${LOG_FILTERS_EXTENSION_VERSION}.jar
ARG LOG_FILTERS_EXTENSION_DEPLOYMENT_JAR_PATH=/home/extension/deployment/target/log-filters-extension-deployment-${LOG_FILTERS_EXTENSION_VERSION}.jar

WORKDIR /opt/keycloak
# Add log-filters-extension to keycloak
COPY --from=log-filters-extension $LOG_FILTERS_EXTENSION_RUNTIME_JAR_PATH /opt/keycloak/providers
COPY --from=log-filters-extension $LOG_FILTERS_EXTENSION_DEPLOYMENT_JAR_PATH /opt/keycloak/providers

COPY quarkus.properties conf/
# Enable health and metrics support
ENV KC_HEALTH_ENABLED=true
ENV KC_METRICS_ENABLED=true

# Configure a database vendor
ENV KC_DB=postgres
WORKDIR /opt/keycloak
RUN /opt/keycloak/bin/kc.sh build

FROM quay.io/keycloak/keycloak:latest
COPY --from=builder /opt/keycloak/ /opt/keycloak/
ENTRYPOINT ["/opt/keycloak/bin/kc.sh", "start-dev"]