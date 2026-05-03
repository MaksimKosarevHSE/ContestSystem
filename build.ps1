$ErrorActionPreference = "Stop"

mvn -s .mvn/settings.xml -B clean package -DskipTests