# Hibatis

## 在spring中配置
```xml
<bean id="sqlSessionFactory" class="com.yaoa.hibatis.SqlSessionFactoryBean">
     <property name="dataSource" ref="druidDataSource" />
</bean>
<bean class="com.yaoa.hibatis.config.HibatisConfiguration"/>
```
##  如何使用
1、声明实体,使用注解@com.yaoa.hibatis.annotation.Entity
```java
@Entity(cacheable = false)
@Table(name = "act_activity")
public class Activity {

	@Id
	private long id;
	
	private String name ; 
}
```
2、如何使用
```java
public class HibatisBaseDao<T> {

	@Autowired
	private SqlMapperTemplate mapper;
	
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
	
	///////////////////////  SQL操作 ////////////////////////
	
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
