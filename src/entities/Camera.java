package entities;

import vector.Vector3f;

public abstract class Camera{

	public abstract Vector3f getPosition();

	public abstract float getPitch();

	public abstract float getYaw();

	public abstract float getRoll();
	
	public abstract void move();
	
	/*private class CameraInput {
		private float mouseSensitivity = 0.4f;
		private DoubleBuffer b1 = BufferUtils.createDoubleBuffer(1);
		private DoubleBuffer b2 = BufferUtils.createDoubleBuffer(1);
		private double mouseX = 0;
		private double mouseY = 0;

		public CameraInput() {
			glfwGetCursorPos(Display.getWindow(), b1, b2);
			mouseX = b1.get(0);
			mouseY = b2.get(0);
		}

		public void getInput(Terrain terrain) {
			glfwGetCursorPos(Display.getWindow(), b1, b2);
			double currX = b1.get(0);
			double currY = b2.get(0);

			if (GLFW.glfwGetKey(Display.getWindow(), GLFW_KEY_Q) == 1) {
				roll -= 5f;
			}
			if (GLFW.glfwGetKey(Display.getWindow(), GLFW_KEY_E) == 1) {
				roll += 5f;
			}
			rc = (float) Math.cos(Math.PI * roll / 180f);
			rs = (float) Math.sin(Math.PI * roll / 180f);
			float mouseDx = (float) (currX - mouseX) * mouseSensitivity;
			float mouseDy = (float) (currY - mouseY) * mouseSensitivity;
			yaw += mouseDx * rc - mouseDy * rs;
			pitch += mouseDy * rc + mouseDx * rs;
			if (pitch > 90.0f) {
				pitch = 90.0f;
			}
			if (pitch < -90.0f) {
				pitch = -90.0f;
			}
			yc = (float) Math.cos(Math.PI * yaw / 180f);
			ys = (float) Math.sin(Math.PI * yaw / 180f);
			pc = (float) Math.cos(Math.PI * pitch / 180f);
			ps = (float) Math.sin(Math.PI * pitch / 180f);

			if (GLFW.glfwGetKey(Display.getWindow(), GLFW_KEY_W) == 1) {
				position.z -= speed * yc * pc;
				position.x += speed * ys * pc;
				position.y -= speed * ps;
			}
			if (GLFW.glfwGetKey(Display.getWindow(), GLFW_KEY_S) == 1) {
				position.z += speed * yc * pc;
				position.x -= speed * ys * pc;
				position.y += speed * ps;
			}
			if (GLFW.glfwGetKey(Display.getWindow(), GLFW_KEY_A) == 1) {
				position.z -= speed * ys * rc;
				position.x -= speed * yc * rc;
				position.y += speed * rs;
			}
			if (GLFW.glfwGetKey(Display.getWindow(), GLFW_KEY_D) == 1) {
				position.z += speed * ys * rc;
				position.x += speed * yc * rc;
				position.y -= speed * rs;
			}
			if (GLFW.glfwGetKey(Display.getWindow(), GLFW_KEY_SPACE) == 1) {
				// position.z += fraction*rs*ys*ps;
				// position.x += fraction*rs*yc;
				if (!isJumping) {
					if (speed < 1.0f) {
						vy = 1.5f;
					} else {
						vy = 5.0f;
					}
					isJumping = true;
				}

				// position.y += speed;
			}
			if (GLFW.glfwGetKey(Display.getWindow(), GLFW_KEY_LEFT_CONTROL) == 1) {
				// position.z += fraction*rs*ys*ps;
				// position.x -= fraction*rs*yc;
				// position.y -= speed;
				height = 1.0f;
			} else {
				height = (float) Math.min(10.5f, height + 0.5);
			}
			if (GLFW.glfwGetKey(Display.getWindow(), GLFW.GLFW_KEY_LEFT_SHIFT) == 1) {
				speed = sprintSpeed;
			} else {
				speed = walkSpeed;
			}
			mouseX = currX;
			mouseY = currY;
			 vy -= 0.05f;
			position.y += vy;
			float terrainHeight = terrain
					.getHeightOfTerrain(position.x, position.z);
			if (position.y < terrainHeight + height) {
				 position.y = terrainHeight + height;
				 vy = 0;
				 isJumping = false;
			}
		}
	}*/
}