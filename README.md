
A  demo for tis Plugin developing https://tis.pub/docs/develop/plugin-develop-detail

1. Generate Plugin archetype by Maven command 

   ```shell
    mvn com.qlangtech.tis:tis-archetype-generate-plugin:$version:generate \
     -Drat.skip=true  \
     -Dtis.version=$version \
     -Dtis.extendpoint="com.qlangtech.tis.plugin.ds.DataSourceFactory:MySQLV5DataSourceFactory" \
     -Dtis.artifactId=tis-mysql-ds-v5-plugin
   ```

2. Launch Plugin Test

   ```shell
    mvn com.qlangtech.tis:tis-archetype-run-plugin:run
   ```
3. Clean local template Dir for test

   ```shell
    mvn com.qlangtech.tis:tis-archetype-run-plugin:clean
   ```

