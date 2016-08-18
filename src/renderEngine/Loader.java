package renderEngine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL33;

import models.Model;
import textures.TextureData;
import util.AABB;
import util.Utils;

public class Loader {
	
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	public Model loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents, int[] indicies){
		int vaoId = createVAO();
		bindIndiciesBuffer(indicies);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		storeDataInAttributeList(3, 3, tangents);
		unbindVAO();
		return new Model(vaoId, indicies.length, Utils.getBoundingSphere(positions), Utils.getBoundingBox(positions));
	}
	
	public Model loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indicies){
		int vaoId = createVAO();
		bindIndiciesBuffer(indicies);
		storeDataInAttributeList(0, 3, positions);
		storeDataInAttributeList(1, 2, textureCoords);
		storeDataInAttributeList(2, 3, normals);
		unbindVAO();
		return new Model(vaoId, indicies.length, Utils.getBoundingSphere(positions), Utils.getBoundingBox(positions));
	}
	
	public int createEmptyVBO(int floatCount){
		int vbo = GL15.glGenBuffers();
		vbos.add(vbo);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, floatCount * 4, GL15.GL_STREAM_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		return vbo;
	}
	
	public void addInstancedAttribute(int vao, int vbo, int attribute, int dataSize, int instancedDataLength, int offset){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL30.glBindVertexArray(vao);
		GL20.glVertexAttribPointer(attribute, dataSize, GL11.GL_FLOAT, false, instancedDataLength * 4, offset * 4);
		GL33.glVertexAttribDivisor(attribute, 1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL30.glBindVertexArray(0);
	}
	
	public void updateVBO(int vbo, float[] data, FloatBuffer buffer){
		buffer.clear();
		buffer.put(data);
		buffer.flip();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer.capacity(), GL15.GL_STREAM_DRAW);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, buffer);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public Model loadToVAO(float[] positions, int dimensions){
		int vaoId = createVAO();
		storeDataInAttributeList(0, dimensions, positions);
		unbindVAO();
		return new Model(vaoId, positions.length / dimensions, 0, new AABB());
	}
	
	public Model loadToVAO(float[] positions){
		int vaoID = createVAO();
		this.storeDataInAttributeList(0, 2, positions);
		unbindVAO();
		return new Model(vaoID, positions.length/2, 0, null);
	}
	
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	
	public int loadCubeMap(String[] textureFiles){
		int texID = GL11.glGenTextures();
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP,  texID);
		for(int i = 0; i <textureFiles.length; i++){
			TextureData data = decodeTextureFile("res/"+textureFiles[i] + ".png");
			GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(),
				0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
		}
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
		textures.add(texID);
		return texID;
	}
	
	private TextureData decodeTextureFile(String filename){
		int width = 0;
		int height = 0;
		BufferedImage image = null;
		ByteBuffer buffer = null;
		try {
			image = ImageIO.read(new File(filename));
			int[] pixels = new int[image.getWidth() * image.getHeight()];
	        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());
			width = image.getWidth();
			height = image.getHeight();
	        buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

	        for(int y = 0; y < image.getHeight(); y++){
	            for(int x = 0; x < image.getWidth(); x++){
	                int pixel = pixels[y * image.getWidth() + x];
	                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
	                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
	                buffer.put((byte) (pixel & 0xFF));               // Blue component
	                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
	            }
	        }
	        buffer.flip();
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failed to load texture " + filename);
			System.exit(-1);
		}
		return new TextureData(buffer, width, height);
	}
	
	public int loadTexture(String filename){
		int textureID;
		BufferedImage image = null;
		ByteBuffer buffer = null;
		try {
			image = ImageIO.read(new File("res/"+filename+".png"));
			int[] pixels = new int[image.getWidth() * image.getHeight()];
	        image.getRGB(0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth());

	        buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4); //4 for RGBA, 3 for RGB

	        for(int y = 0; y < image.getHeight(); y++){
	            for(int x = 0; x < image.getWidth(); x++){
	                int pixel = pixels[y * image.getWidth() + x];
	                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
	                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
	                buffer.put((byte) (pixel & 0xFF));               // Blue component
	                buffer.put((byte) ((pixel >> 24) & 0xFF));    // Alpha component. Only for RGBA
	            }
	        }
	        buffer.flip();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

      	textureID = GL11.glGenTextures();
		textures.add(textureID);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureID); //Bind texture ID

        //Setup wrap mode
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

        //Setup texture scaling filtering
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

        //Send texel data to OpenGL
		GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, image.getWidth(), image.getHeight(), 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		
		GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_LOD_BIAS, -0.4f);
		
		return textureID;
	}
	
	private void storeDataInAttributeList(int attribID, int coordinateSize, float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attribID, coordinateSize, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	
	public void cleanUp(){
		for(int vao : vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo : vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture : textures){
			GL11.glDeleteTextures(texture);
		}
	}
	
	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	
	private void bindIndiciesBuffer(int[] indicies){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indicies);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		
	}
	
	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
} 
