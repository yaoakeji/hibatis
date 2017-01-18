# Hibatis
	Hibatis ，轻量级、高并发、分布式数据库框架 ，是 一套融合Ibatis和JPA优点的ORM框架，完美兼容JPA和SQLMapper两种ORM书写方式。
	对比Hibernate优势
	1、完美兼容JPA和SQLMapper两种ORM书写方式
	2、不使用session缓存，直接采用全局缓存方式，避免session级缓存带来的额外内存开销，因为实际应用时很少重复去get/load对象。
	3、为简捷开发，不完全遵循JPA标准，结合JPA和Ibatis特性重新制定JPA；开发效率高，易理解。
	4、避免了Hibernate-JPA常规性异常，如对象关联、懒加载等。
	5、直接瞬时状态实体执行Update，框架会自动把瞬时状态实体和持久对象进行属性复制合并，避免get/load操作带来的烦恼。
	6、灵活简易的全局缓存机制，支持Memory、Redis、Memcached等分布式缓存。
	7、支持分布式对象锁，避免数据for update 带来的数据库额外消耗和高并发问题。
## 在spring中配置
```xml
<bean id="druidDataSource" class="com.alibaba.druid.pool.DruidDataSource" init-method="init" destroy-method="close">
	<property name="url" value="jdbc:mysql://127.0.0.1:3306/test?useUnicode=true&amp;characterEncoding=UTF-8" />
	<property name="username" value="root" />
	<property name="password" value="" />
</bean>
<bean id="sqlSessionFactory" class="com.yaoa.hibatis.SqlSessionFactoryBean">
     <property name="dataSource" ref="druidDataSource" />
</bean>
<bean class="com.yaoa.hibatis.config.HibatisConfiguration"/>

<bean id="transactionManager" class="com.yaoa.hibatis.transaction.HibatisDataSourceTransactionManager">
        <property name="dataSource" ref="druidDataSource" />
</bean>

<tx:annotation-driven transaction-manager="transactionManager"/>
```
##  如何使用
### 定义实体
1、声明实体：使用注解@com.yaoa.hibatis.annotation.Entity
2、声明主键：使用注解@com.yaoa.hibatis.annotation.Id
3、表名和列名默认采用骆驼命名法，如果需要修改以下注解
自定义表名 @com.yaoa.hibatis.annotation.Table
自定义表名 @com.yaoa.hibatis.annotation.Column
```java
@Entity(cacheable = false)
@Table(name = "act_activity")
public class Activity {

	@Id
	private long id;
	
	private String name ; 
}
```
### 数据库操作
```java
public class HibatisBaseDao<T> {

	@Autowired
	private SqlMapperTemplate mapper;
	
	///////////////////////  实体 操作 ////////////////////////
	
	public T findById(Serializable id) {
		return mapper.findById(entityType, id);
	}
	
	public T findById(Serializable id , LockMode lockMode) {
		return mapper.findById(entityType, id ,  lockMode);
	}
	
	public void refresh(T entity) {
		mapper.refresh(entity, LockMode.NONE);
	}
	
	public void refresh(T entity , LockMode lockMode) {
		mapper.refresh(entity, lockMode);
	}
	
	public long count(Criterion criterion) {
		if(criterion == null){
			criterion = CriterionBuilder.create(entityType).build();
		}
		return mapper.count(criterion);
	}
	
	public <R> R aggregate(Aggregate aggregate , Class<R> resultType) {
		return mapper.aggregate(aggregate, resultType);
	} 
	
	public <R> List<R> aggregateList(Aggregate aggregate , Class<R> resultType) {
		return mapper.aggregateList(aggregate, resultType);
	} 
	
	public QueryResult<T> query(QueryForm form) {
		QueryResult<T> result = null;
		QueryFormCriterionBuilder builder = new QueryFormCriterionBuilder(entityType, form);
		Criterion criterion = builder.getCriterion();
		if (form instanceof PaginationForm) {
			Page<T> page = mapper.paging(criterion);
			String orderBy = form.getOrderBy();
			List<T> list = page.getContent();
			long total = page.getTotal();
			int pageSize = page.getPageSize();
			int currentPage = page.getCurrentPage();
			result = new PaginationResult<>(list, orderBy, pageSize, currentPage, total);
		} else {
			List<T> list = mapper.find(criterion);
			String orderBy = form.getOrderBy();
			result = new SimpleQueryResult<>(list, orderBy);
		}
		return result;
	}
	
	public PaginationResult<T> paging(Criterion criterion) {
		String orderBy = "";
		Page<T> page = mapper.paging(criterion);
		List<T> list = page.getContent();
		long total = page.getTotal();
		int pageSize = page.getPageSize();
		int currentPage = page.getCurrentPage();
		return new PaginationResult<>(list, orderBy, pageSize, currentPage, total);
	}
	
	public List<T> query(Criterion criterion) {
		return mapper.find(criterion);
	}
	
	public T findOne(Criterion criterion) {
		criterion.setFirstResult(0);
		criterion.setMaxResults(1);
		List<T> list = mapper.find(criterion);
		if(list.size() == 0){
			return null;
		}else{
			return list.get(0);
		}
	}
	
	public List<T> find(Criterion criterion) {
		return this.query(criterion);
	}
	
	public List<T> findAll() {
		Criterion criterion = CriterionBuilder.create(entityType).build();
		return this.query(criterion);
	}
	
	public T insert(T entity) {
		return mapper.insert(entity);
	}
	
	public int update(T entity) {
		return mapper.update(entity);
	}
	
	public T save(T entity) {
		return mapper.save(entity);
	}
	
	public int delete(T entity) {
		return mapper.delete(entity);
	}
	
	public int delete(Criterion criterion){
		return mapper.delete(criterion);
	}
	
	public int deleteById(Object... id) {
		return mapper.deletebyId(entityType, id);
	}
	
	///////////////////////  SQLMapper 操作 ////////////////////////
	
	public <R> R selectOne(String statement) {
		return this.selectOne(statement, null);
	}
	
	public <R> R selectOne(String statement, Object parameter) {
		return mapper.selectOne(entityType.getName() + "." + statement, parameter);
	}
	
	public <R> List<R> selectList(String statement) {
		return mapper.selectList(entityType.getName() + "." + statement, null);
	}
	
	public <R> List<R>  selectList(String statement, Object parameter) {
		return mapper.selectList(entityType.getName() + "." + statement, parameter);
	}
	
	public int insert(String statement, T entity) {
		return mapper.insert(entityType, entityType.getName() + "." + statement, entity);
	}
	
	public int update(String statement, Object parameter) {
		return mapper.update(entityType,entityType.getName() + "." + statement,parameter);
	}
	
	public int delete(Class<T> entityClass, String statement, T parameter) {
		return mapper.delete(entityClass, entityClass.getName() + "." + statement, parameter);
	}
	
}
```
