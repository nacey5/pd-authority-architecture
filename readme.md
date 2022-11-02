#模块介绍
~~~
pinda-authority              #聚合工程，用于聚合pd-parent、pd-apps、pd-tools等模块
├── pd-parent				 # 父工程，nacos配置及依赖包管理
├── pd-apps					 # 应用目录
	├── pd-auth				 # 权限服务父工程
		├── pd-auth-entity   # 权限实体
		├── pa-auth-server   # 权限服务
	├── pd-gateway			 # 网关服务
└── pd-tools				 # 工具工程
	├── pd-tools-common		 # 基础组件：基础配置类、函数、常量、统一异常处理、undertow服务器
	├── pd-tools-core		 # 核心组件：基础实体、返回对象、上下文、异常处理、分布式锁、函数、树
	├── pd-tools-databases	 # 数据源组件：数据源配置、数据权限、查询条件等
	├── pd-tools-dozer		 # 对象转换：dozer配置、工具
	├── pd-tools-j2cache	 # 缓存组件：j2cache、redis缓存
	├── pd-tools-jwt         # JWT组件：配置、属性、工具
	├── pd-tools-log	     # 日志组件：日志实体、事件、拦截器、工具
	├── pd-tools-swagger2	 # 文档组件：knife4j文档
	├── pd-tools-user        # 用户上下文：用户注解、模型和工具，当前登录用户信息注入模块
	├── pd-tools-validator	 # 表单验证： 后台表单规则验证
	├── pd-tools-xss		 # xss防注入组件
~~~
项目搭建的基础模块--项目有关的开发
且这是一个较为完整的模块，个性化的开发也可从此基础上进行架构搭建

nacos: `sh startup.sh -m standalone`  
address: `192.168.74.135`  
port: `8848`  
设置JAVA_HOME
~~~shell script
export JAVA_HOME=/opt/jdk1.8.0_351
export PATH=$PATH:$JAVA_HOME/bin
source /etc/profile
~~~
配置文件修改  
~~~properties
spring.datasource.platform=mysql
db.num=1
db.url.0=jdbc:mysql://192.168.74.1:3306/nacos?characterEncoding=utf8&connectTimeout=1000&socketTimeout=3000&autoReconnect=true&useUnicode=true&useSSL=false&serverTimezone=UTC
~~~  
启动后的访问地址 `http://192.168.74.135:8848/nacos/index.html`  
数据源直接采用nacos进行配置导入  
common.yml
~~~yaml
server:
  undertow: # jetty  undertow
    io-threads: 8 # 设置IO线程数, 它主要执行非阻塞的任务,它们会负责多个连接, 默认设置每个CPU核心一个线程
    worker-threads: 120  # 阻塞任务线程池, 当执行类似servlet请求阻塞操作, undertow会从这个线程池中取得线程,它的值设置取决于系统的负载
    buffer-size: 2048  # 以下的配置会影响buffer,这些buffer会用于服务器连接的IO操作,有点类似netty的池化内存管理 , 每块buffer的空间大小,越小的空间被利用越充分
    direct-buffers: true  # 是否分配的直接内存

spring:
  http:
    encoding:
      charset: UTF-8
      force: true
      enabled: true
  servlet:
    multipart:
      max-file-size: 512MB      # Max file size，默认1M
      max-request-size: 512MB   # Max request size，默认10M

dozer:
  mappingFiles:
    - classpath:dozer/global.dozer.xml
    - classpath:dozer/biz.dozer.xml
management:
  endpoints:
    web:
      base-path: /actuator
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: ALWAYS
      enabled: true

feign:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
  hystrix:
    enabled: true   # feign 熔断机制是否开启
    #支持压缩的mime types
  compression:  # 请求压缩
    request:
      enabled: true
      mime-types: text/xml,application/xml,application/json
      min-request-size: 2048
    response:  # 响应压缩
      enabled: true

ribbon:
  httpclient:
    enabled: false
  okhttp:
    enabled: true
  eureka:
    enabled: true
  ReadTimeout: 30000     #
  ConnectTimeout: 30000  # [ribbon超时时间]大于[熔断超时],那么会先走熔断，相当于你配的ribbon超时就不生效了  ribbon和hystrix是同时生效的，哪个值小哪个生效
  MaxAutoRetries: 0             # 最大自动重试
  MaxAutoRetriesNextServer: 1   # 最大自动像下一个服务重试
  OkToRetryOnAllOperations: false  #无论是请求超时或者socket read timeout都进行重试，

hystrix:
  threadpool:
    default:
      coreSize: 1000 # #并发执行的最大线程数，默认10
      maxQueueSize: 1000 # #BlockingQueue的最大队列数
      queueSizeRejectionThreshold: 500 # #即使maxQueueSize没有达到，达到queueSizeRejectionThreshold该值后，请求也会被拒绝
  command:
    default:
      execution:
        isolation:
          thread:
            timeoutInMilliseconds: 120000  # 熔断超时 ribbon和hystrix是同时生效的，哪个值小哪个生效

id-generator:
  machine-code: 1  # id生成器机器掩码
~~~
mysql.yml  
~~~yaml
# mysql 个性化配置， 不同的环境，需要配置不同的链接信息，只需要将这段信息复制
# 到具体环境的配置文件中进行修改即可
# 如：复制到pd-auth-server-dev.yml中将数据库名和ip改掉
pinda:
  mysql:
    ip: 127.0.0.1
    port: 3306
    driverClassName: com.mysql.cj.jdbc.Driver
    database: pd_auth
    username: root
    password: root
  database:
    isBlockAttack: false  # 是否启用 攻击 SQL 阻断解析器

# mysql 通用配置
spring:
  datasource:
    druid:
      username: ${pinda.mysql.username}
      password: ${pinda.mysql.password}
      driver-class-name: ${pinda.mysql.driverClassName}
      url: jdbc:mysql://${pinda.mysql.ip}:${pinda.mysql.port}/${pinda.mysql.database}?serverTimezone=CTT&characterEncoding=utf8&useUnicode=true&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true
      db-type: mysql
      initialSize: 10
      minIdle: 10
      maxActive: 500
      max-wait: 60000
      pool-prepared-statements: true
      max-pool-prepared-statement-per-connection-size: 20
      validation-query: SELECT 'x'
      test-on-borrow: false
      test-on-return: false
      test-while-idle: true
      time-between-eviction-runs-millis: 60000  #配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
      min-evictable-idle-time-millis: 300000    #配置一个连接在池中最小生存的时间，单位是毫秒
      filters: stat,wall
      filter:
        wall:
          enabled: true
          config:
            commentAllow: true
            multiStatementAllow: true
            noneBaseStatementAllow: true
      web-stat-filter:  # WebStatFilter配置，说明请参考Druid Wiki，配置_配置WebStatFilter
        enabled: true
        url-pattern: /*
        exclusions: "*.js , *.gif ,*.jpg ,*.png ,*.css ,*.ico , /druid/*"
        session-stat-max-count: 1000
        profile-enable: true
        session-stat-enable: false
      stat-view-servlet:  #展示Druid的统计信息,StatViewServlet的用途包括：1.提供监控信息展示的html页面2.提供监控信息的JSON API
        enabled: true
        url-pattern: /druid/*   #根据配置中的url-pattern来访问内置监控页面，如果是上面的配置，内置监控页面的首页是/druid/index.html例如：http://127.0.0.1:9000/druid/index.html
        reset-enable: true    #允许清空统计数据
        login-username: pinda
        login-password: pinda

mybatis-plus:
  mapper-locations:
    - classpath*:mapper_**/**/*Mapper.xml
  #实体扫描，多个package用逗号或者分号分隔
  typeAliasesPackage: com.itheima.pinda.*.entity;com.itheima.pinda.database.mybatis.typehandler
  global-config:
    db-config:
      id-type: INPUT
      insert-strategy: NOT_NULL
      update-strategy: NOT_NULL
      select-strategy: NOT_EMPTY
  configuration:
    #配置返回数据库(column下划线命名&&返回java实体是驼峰命名)，
    #自动匹配无需as（没开启这个，SQL需要写as： select user_id as userId）
    map-underscore-to-camel-case: true
    cache-enabled: false
    #配置JdbcTypeForNull, oracle数据库必须配置
    jdbc-type-for-null: 'null'
~~~  
pd-auth-server-dev.yml
~~~yaml
# p6spy是一个开源项目，通常使用它来跟踪数据库操作，查看程序运行过程中执行的sql语句
# 开发环境需要使用p6spy进行sql语句输出
# 但p6spy会有性能损耗，不适合在生产线使用，故其他环境无需配置
spring:
  datasource:
    driver-class-name: com.p6spy.engine.spy.P6SpyDriver
    url: jdbc:p6spy:mysql://${pinda.mysql.ip}:${pinda.mysql.port}/${pinda.mysql.database}?serverTimezone=CTT&characterEncoding=utf8&useUnicode=true&useSSL=false&autoReconnect=true&zeroDateTimeBehavior=convertToNull&allowMultiQueries=true
    db-type: mysql

~~~  
pd-auth-server.yml
~~~yaml
# 在这里配置 权限服务 所有环境都能使用的配置
pinda:
  mysql:
    database: pd_auth
  swagger:
    enabled: true
    docket:
      auth:
        title: 权限模块
        base-package: com.itheima.pinda.authority.controller.auth
      common:
        title: 公共模块
        base-package: com.itheima.pinda.authority.controller.common
      core:
        title: 组织岗位模块
        base-package: com.itheima.pinda.authority.controller.core

authentication:
  user:
    header-name: token
    expire: 43200               # 外部token有效期为12小时
    pri-key: client/pri.key    # 加密
    pub-key: client/pub.key    # 解密

server:
  port: 8764
~~~  
pd-gateway.yml
~~~yaml
pinda:
  log:
    enabled: false

spring:
  mvc:
    servlet:
      path: /gate

server:
  port: 8760
  servlet:
    context-path: /api  # = server.servlet.context-path

zuul:
  #  debug:
  #    request: true
  #  include-debug-header: true
  retryable: false
  servlet-path: /         # 默认是/zuul , 上传文件需要加/zuul前缀才不会出现乱码，这个改成/ 即可不加前缀
  ignored-services: "*"   # 忽略eureka上的所有服务
  sensitive-headers:  # 一些比较敏感的请求头，不想通过zuul传递过去， 可以通过该属性进行设置
  #  prefix: /api #为zuul设置一个公共的前缀
  #  strip-prefix: false     #对于代理前缀默认会被移除   故加入false  表示不要移除
  routes:  # 路由配置方式
    authority:  # 其中 authority 是路由名称，可以随便定义，但是path和service-id需要一一对应
      path: /authority/**
      serviceId: pd-auth-server
    file:
      path: /file/**
      serviceId: pd-file-server


authentication:
  user:
    header-name: token
    pub-key: client/pub.key    # 解密
~~~  
redis.yml
~~~yaml
# redis 通用配置， 不同的环境，需要配置不同的链接信息，
# 只需要将这段信息复制到具体环境的配置文件中进行修改即可
# 如：复制到pd-auth-server-dev.yml中将数据库名和ip改掉
pinda:
  redis:
    ip: 127.0.0.1
    port: 6379
    password:
    database: 0

spring:
  cache:
    type: GENERIC
  redis:
    host: ${pinda.redis.ip}
    password: ${pinda.redis.password}
    port: ${pinda.redis.port}
    database: ${pinda.redis.database}

j2cache:
  #  config-location: /j2cache.properties
  open-spring-cache: true
  cache-clean-mode: passive
  allow-null-values: true
  redis-client: lettuce
  l2-cache-open: true
  # l2-cache-open: false     # 关闭二级缓存
  broadcast: net.oschina.j2cache.cache.support.redis.SpringRedisPubSubPolicy
  #  broadcast: jgroups       # 关闭二级缓存
  L1:
    provider_class: caffeine
  L2:
    provider_class: net.oschina.j2cache.cache.support.redis.SpringRedisProvider
    config_section: lettuce
  sync_ttl_to_redis: true
  default_cache_null_object: false
  serialization: fst
caffeine:
  properties: /j2cache/caffeine.properties   # 这个配置文件需要放在项目中
lettuce:
  mode: single
  namespace:
  storage: generic
  channel: j2cache
  scheme: redis
  hosts: ${pinda.redis.ip}:${pinda.redis.port}
  password: ${pinda.redis.password}
  database: ${pinda.redis.database}
  sentinelMasterId:
  maxTotal: 100
  maxIdle: 10
  minIdle: 10
  timeout: 10000
~~~
~~~sql
~~~