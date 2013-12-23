package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;

/**
 * total cut and paste from:  
 * http://stackoverflow.com/questions/16027455/what-is-the-easiest-way-to-draw-line-using-opengl-es-android
 */
public class Line {

	private FloatBuffer VertexBuffer;

	private final String vShaderCode = "uniform mat4 uMVPMatrix;"
			+ "attribute vec4 vPosition;" + "void main() {"
			+ "  gl_Position = uMVPMatrix * vPosition;" + "}";

	private final String fShaderCode = "precision mediump float;"
			+ "uniform vec4 vColor;" + "void main() {"
			+ "  gl_FragColor = vColor;" + "}";

	protected int GlProgram;
	protected int PositionHandle;
	protected int ColorHandle;
	protected int MVPMatrixHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static float LineCoords[] = { 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f };

	private final int VertexCount = LineCoords.length / COORDS_PER_VERTEX;
	private final int VertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
															// vertex

	// Set color with red, green, blue and alpha (opacity) values
	float color[] = { 0.0f, 1.0f, 0.0f, 1.0f };

	public Line() {
		ByteBuffer bb = ByteBuffer.allocateDirect(LineCoords.length * 4);
		bb.order(ByteOrder.nativeOrder()); // use the device hardware's native byte order
		VertexBuffer = bb.asFloatBuffer(); // create a floating point buffer from the ByteBuffer
		
		VertexBuffer.put(LineCoords); // add the coordinates to the FloatBuffer
		VertexBuffer.position(0); // set the buffer to read the first coordinate

		int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vShaderCode);
		int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fShaderCode);

		GlProgram = GLES20.glCreateProgram(); // create empty OpenGL ES Program
		GLES20.glAttachShader(GlProgram, vertexShader); // add the vertex shader to program
		GLES20.glAttachShader(GlProgram, fragmentShader); // add the fragment shader to program
		GLES20.glLinkProgram(GlProgram); // creates OpenGL ES program  executables

	}

	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL ES environment
		GLES20.glUseProgram(GlProgram);

		// get handle to vertex shader's vPosition member
		PositionHandle = GLES20.glGetAttribLocation(GlProgram, "vPosition");

		// Enable a handle to the triangle vertices
		GLES20.glEnableVertexAttribArray(PositionHandle);

		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(PositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, VertexStride, VertexBuffer);

		// get handle to fragment shader's vColor member
		ColorHandle = GLES20.glGetUniformLocation(GlProgram, "vColor");

		// Set color for drawing the triangle
		GLES20.glUniform4fv(ColorHandle, 1, color, 0);

		// get handle to shape's transformation matrix
		MVPMatrixHandle = GLES20.glGetUniformLocation(GlProgram, "uMVPMatrix");
		MyGLRenderer.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(MVPMatrixHandle, 1, false, mvpMatrix, 0);
		MyGLRenderer.checkGlError("glUniformMatrix4fv");

		// Draw the triangle
		GLES20.glDrawArrays(GLES20.GL_LINES, 0, VertexCount);

		// Disable vertex array
		GLES20.glDisableVertexAttribArray(PositionHandle);
	}
}