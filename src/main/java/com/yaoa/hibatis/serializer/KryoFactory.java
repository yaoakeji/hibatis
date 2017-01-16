package com.yaoa.hibatis.serializer;

import java.util.LinkedHashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.esotericsoftware.kryo.Kryo;

/**
 * @Description Kryo序列化工厂
 * @author cjh
 * @version 1.0
 * @date：2016年8月31日 下午6:53:47
 */
public class KryoFactory {

    private static final KryoFactory factory = new KryoFactory();

    private final Set<Class<?>> registrations = new LinkedHashSet<Class<?>>();
    
    private final Queue<Kryo> pool = new ConcurrentLinkedQueue<Kryo>();

    private boolean kryoReferences;

    private KryoFactory(){}

    public void registerClass(Class<?> clazz) {
        registrations.add(clazz);
    }

    protected Kryo createKryo() {
        Kryo kryo = new Kryo();
        for (Class<?> clazz : registrations) {
            kryo.register(clazz);
        }
        return kryo;
    }
    
    public void returnKryo(Kryo kryo) {
        pool.offer(kryo);
    }

    public void close() {
        pool.clear();
    }

    public Kryo getKryo() {
        Kryo kryo = pool.poll();
        if (kryo == null) {
            kryo = createKryo();
        }
        return kryo;
    }

    public static KryoFactory getFactory() {
        return factory;
    }

	public boolean isKryoReferences() {
		return kryoReferences;
	}

	public void setKryoReferences(boolean kryoReferences) {
		this.kryoReferences = kryoReferences;
	}

}