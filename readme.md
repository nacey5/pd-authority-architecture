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