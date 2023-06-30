
A  demo for tis Plugin developing https://tis.pub/docs/develop/plugin-develop-detail

1. Generate Plugin archetype by Maven command 

First add profile as below in file with path `$MAVEN_HOME/conf/setting.xml`:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings>
 <profiles>
  <profile>
            <id>tis</id>
            <repositories>
               <repository>
                    <id>tis-releases</id>
                    <url>http://mvn-repo.oss-cn-hangzhou.aliyuncs.com/release/</url>
                </repository>
            </repositories>
            <pluginRepositories>
               <pluginRepository>
                    <id>tis-releases</id>
                    <url>http://mvn-repo.oss-cn-hangzhou.aliyuncs.com/release/</url>
                </pluginRepository>
            </pluginRepositories>
  </profile>
 </profiles>
</settings>
```


   ```shell
    mvn com.qlangtech.tis:tis-archetype-generate-plugin:$version:generate \
     -Drat.skip=true  \
     -Dtis.version=$version \
     -Dtis.extendpoint="com.qlangtech.tis.plugin.ds.DataSourceFactory:MySQLV5DataSourceFactory" \
     -Dtis.artifactId=tis-mysql-ds-v5-plugin \
     -Ptis
   ```

2. Launch Plugin Test

   ```shell
    mvn com.qlangtech.tis:tis-archetype-run-plugin:run
   ```
3. Clean local template Dir for test

   ```shell
    mvn com.qlangtech.tis:tis-archetype-run-plugin:clean
   ```

