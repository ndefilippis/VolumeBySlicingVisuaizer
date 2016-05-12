package terrain;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import models.Model;
import renderEngine.Loader;
import util.Utils;
import vector.Vector2f;
import vector.Vector3f;

public class Terrain {
	private static final float SIZE = 1600f;
	private static final float MAX_HEIGHT = 200f;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;

	private float x;
	private float z;
	private Model model;
	private TerrainTexturePack texturePack;
	private TerrainTexture blendMap;

	private float[][] heights;

	public Terrain(int gridX, int gridZ, Loader loader, TerrainTexturePack texturePack, TerrainTexture blendMap, String heightMap) {
		this.texturePack = texturePack;
		this.blendMap = blendMap;
		this.x = gridX * SIZE;
		this.z = gridZ * SIZE;
		this.model = generateTerrain(loader, heightMap);
	}

	public float getX() {
		return x;
	}

	public float getZ() {
		return z;
	}

	public Model getModel() {
		return model;
	}

	public TerrainTexturePack getTexturePack() {
		return texturePack;
	}

	public TerrainTexture getBlendMap() {
		return blendMap;
	}

	public float getHeightOfTerrain(float worldX, float worldZ) {
		float terrainX = worldX - this.x;
		float terrainZ = worldZ - this.z;
		float gridSquareSize = SIZE / ((float) (heights.length - 1));
		int gridX = (int) Math.floor(terrainX / gridSquareSize);
		int gridZ = (int) Math.floor(terrainZ / gridSquareSize);
		if (gridX >= heights.length - 1 || gridZ >= heights.length - 1 || gridX < 0 || gridZ < 0) {
			return 0;
		}
		float xCoord = (terrainX % gridSquareSize) / gridSquareSize;
		float zCoord = (terrainZ % gridSquareSize) / gridSquareSize;
		float answer;
		if (xCoord <= (1 - zCoord)) {
			answer = Utils.barryCentric(new Vector3f(0, heights[gridX][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ], 0), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		} else {
			answer = Utils.barryCentric(new Vector3f(1, heights[gridX + 1][gridZ], 0),
					new Vector3f(1, heights[gridX + 1][gridZ + 1], 1), new Vector3f(0, heights[gridX][gridZ + 1], 1),
					new Vector2f(xCoord, zCoord));
		}
		return answer;

	}
	
	private static final int[] permutation = { 151,160,137,91,90,15,                 // Hash lookup table as defined by Ken Perlin.  This is a randomly
	    131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,    // arranged array of all numbers from 0-255 inclusive.
	    190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
	    88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
	    77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
	    102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
	    135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
	    5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
	    223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
	    129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
	    251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
	    49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
	    138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
	};
	
	private static int[] p = new int[512];
	
	private float cerp(float a, float b, float x){
		float ft = (float) (x * Math.PI);
		float f = (float) ((1 - Math.cos(ft))*0.5);
		return a * (1-f) + b*f;
	}
	
	private float perlinNoise(float x, float y, float z){
		int xi = (int)x & 255;
		int yi = (int)y & 255;
		int zi = (int)z & 255;
		
		float xf = x-(int)x;
		float yf = y-(int)y;
		float zf = z-(int)z;
		float u = fade(xf);
		float v = fade(yf);
		float w = fade(zf);
		int aaa, aba, aab, abb, baa, bba, bab, bbb;
	    aaa = p[p[p[    xi ]+    yi ]+    zi ];
	    aba = p[p[p[    xi ]+ yi + 1]+    zi ];
	    aab = p[p[p[    xi ]+    yi ]+ zi + 1];
	    abb = p[p[p[    xi ]+ yi + 1]+ zi + 1];
	    baa = p[p[p[xi	+ 1]+    yi ]+    zi ];
	    bba = p[p[p[xi 	+ 1]+ yi + 1]+    zi ];
	    bab = p[p[p[xi 	+ 1]+    yi ]+ zi + 1];
	    bbb = p[p[p[xi 	+ 1]+ yi + 1]+ zi + 1];
	    float x1, x2, y1, y2;
	    x1 = cerp(grad(aaa, xf, yf, zf), grad(baa, xf-1, yf, zf), u);
	    x2 = cerp(grad(aba, xf, yf-1, zf), grad(bba, xf-1, yf-1, zf), u);
	    y1 = cerp(x1, x2, v);
	    x1 = cerp(grad(aab, xf, yf, zf-1), grad(bab, xf-1, yf, zf-1), u);
	    x2 = cerp(grad(abb, xf, yf-1, zf-1), grad(bbb, xf-1, yf-1, zf-1), u);
	    y2 = cerp(x1, x2, v);
	    float x11 = (cerp(y1, y2, w)+1)/2;
	    return x11;
	}
	
	public static float lerp(float a, float b, float x) {
	    return a + x * (b - a);
	}
	
	private float octavePerlin(float x, float y, float z, int octaves, double persistence){
		float total = 0;
		float frequency = 1;
		float amplitude = 1;
		float maxValue = 0;
		for(int i = 0; i < octaves; i++){
			total += perlinNoise(x*frequency, y*frequency, z*frequency) * amplitude;
			maxValue += amplitude;
			
			amplitude *= persistence;
			frequency *= 2;
		}
		return total/maxValue;
	}
	
	private float fade(float t){
		return t * t * t * (t * ( t * 6 - 15) + 10);
	}
	private float grad(int hash, float x, float y, float z){
		switch(hash & 0xF){
		case 0x0:	return 	x + y;
		case 0x1:	return -x + y;
		case 0x2:	return 	x - y;
		case 0x3:	return -x - y;
		case 0x4:	return 	x + z;
		case 0x5:	return -x + z;
		case 0x6:	return 	x - z;
		case 0x7:	return -x - z;
		case 0x8:	return 	y + z;
		case 0x9:	return -y + z;
		case 0xA:	return 	y - z;
		case 0xB:	return -y - z;
		case 0xC:	return 	y + x;
		case 0xD:	return -y + z;
		case 0xE:	return 	y - x;
		case 0xF:	return -y - z;
		default:	return 0;
		}
	}

	private Model generateTerrain(Loader loader, String heightMap) {
		for(int i = 0; i < 512; i++){
			p[i] = permutation[i % 256];
		}
		BufferedImage image = null;
		try {
			image = ImageIO.read(new File("res/" + heightMap + ".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int VERTEX_COUNT = image.getHeight();
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		int count = VERTEX_COUNT * VERTEX_COUNT;
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count * 2];
		int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT * 1)];
		int vertexPointer = 0;
		for (int i = 0; i < VERTEX_COUNT; i++) {
			for (int j = 0; j < VERTEX_COUNT; j++) {
				vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
				float height = getHeight(i, j, image);
				heights[j][i] = height;
				vertices[vertexPointer * 3 + 1] = height;
				vertices[vertexPointer * 3 + 2] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
				Vector3f normal = calculateNormal(i, j, image);
				normals[vertexPointer * 3] = normal.x;
				normals[vertexPointer * 3 + 1] = normal.y;
				normals[vertexPointer * 3 + 2] = normal.z;
				textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
				textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
				vertexPointer++;
			}
		}
		int pointer = 0;
		for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
			for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
				int topLeft = (gz * VERTEX_COUNT) + gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
				int bottomRight = bottomLeft + 1;
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		return loader.loadToVAO(vertices, textureCoords, normals, indices);
	}

	private Vector3f calculateNormal(int x, int z, BufferedImage image) {
		float heightL = getHeight(x - 1, z, image);
		float heightR = getHeight(x + 1, z, image);
		float heightD = getHeight(x, z - 1, image);
		float heightU = getHeight(x, z + 1, image);
		Vector3f normal = new Vector3f(heightL - heightR, 2f, heightD - heightU);
		normal.normalise();
		return normal;
	}

	private float getHeight(int x, int z, BufferedImage image) {
		if (x < 0 || x >= image.getHeight() || z < 0 || z >= image.getHeight()) {
			return 0;
		}
		float height = image.getRGB(x, z);
		height += MAX_PIXEL_COLOR / 2f;
		height /= MAX_PIXEL_COLOR / 2f;
		height *= MAX_HEIGHT;
		return height;
	}
}
