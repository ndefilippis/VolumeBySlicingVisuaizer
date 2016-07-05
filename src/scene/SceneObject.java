package scene;

import java.util.ArrayList;

import vector.Matrix4f;

public class SceneObject {
	private ArrayList<SceneObject> children = new ArrayList<SceneObject>();
	private Matrix4f transformationRelativeToParent;	
}
