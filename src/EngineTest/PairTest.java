package EngineTest;

import java.util.HashMap;
import java.util.Map;

import components.BasicEntityManager;
import components.Entity;
import components.EntityManager;
import util.Pair;

public class PairTest {
	public static void main(String[] args){
		Map<Pair<Entity>, Integer> map = new HashMap<Pair<Entity>, Integer>();
		
		EntityManager mgr = new BasicEntityManager();
		Entity e1 = new Entity(mgr);
		Entity e2 = new Entity(mgr);
		map.put(new Pair<Entity>(e1, e2), 0);
		System.out.println(map.containsKey(new Pair<Entity>(e2, e1)));
	}
}
