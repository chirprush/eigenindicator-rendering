package com.FinalProject.util;

import java.util.ArrayList;

import com.FinalProject.events.Event;
import com.FinalProject.events.KeyEvent;
import com.FinalProject.events.MouseEvent;
import com.FinalProject.math.Vector;

import io.github.libsdl4j.api.event.SDL_Event;
import io.github.libsdl4j.api.pixels.SDL_Color;
import io.github.libsdl4j.api.rect.SDL_FPoint;
import io.github.libsdl4j.api.rect.SDL_Rect;
import io.github.libsdl4j.api.render.SDL_Renderer;
import io.github.libsdl4j.api.render.SDL_Vertex;
import io.github.libsdl4j.api.video.SDL_Window;

import static io.github.libsdl4j.api.Sdl.SDL_Init;
import static io.github.libsdl4j.api.Sdl.SDL_Quit;
import static io.github.libsdl4j.api.SdlSubSystemConst.SDL_INIT_EVERYTHING;
import static io.github.libsdl4j.api.error.SdlError.SDL_GetError;
import static io.github.libsdl4j.api.event.SDL_EventType.*;
import static io.github.libsdl4j.api.event.SdlEvents.SDL_PollEvent;
import static io.github.libsdl4j.api.render.SDL_RendererFlags.SDL_RENDERER_ACCELERATED;
import static io.github.libsdl4j.api.render.SdlRender.*;
import static io.github.libsdl4j.api.video.SDL_WindowFlags.SDL_WINDOW_RESIZABLE;
import static io.github.libsdl4j.api.video.SDL_WindowFlags.SDL_WINDOW_SHOWN;
import static io.github.libsdl4j.api.video.SdlVideo.SDL_CreateWindow;
import static io.github.libsdl4j.api.video.SdlVideoConst.SDL_WINDOWPOS_CENTERED;
import static io.github.libsdl4j.api.keyboard.SdlKeyboard.SDL_GetKeyName;
import static io.github.libsdl4j.api.timer.SdlTimer.SDL_Delay;
import static io.github.libsdl4j.api.blendmode.SDL_BlendMode.SDL_BLENDMODE_BLEND;
import static io.github.libsdl4j.api.video.SDL_WindowEventID.SDL_WINDOWEVENT_RESIZED;

// A wrapper for all the somewhat unpleasant looking C SDL bindings.
public class Canvas {
	private SDL_Window window;
	private SDL_Renderer renderer;
	
	private int width;
	private int height;
	
	private boolean running;
	
	public static final Color BG = new Color(0x28, 0x2c, 0x34, 0xff);
	
	public Canvas() {
		if (SDL_Init(SDL_INIT_EVERYTHING) != 0) {
			throw new IllegalStateException("SDL Initialization Error: " + SDL_GetError());
		}
		
		this.width = 800;
		this.height = 600;
		
		this.window = SDL_CreateWindow(
			"AP CS Final Project",
			SDL_WINDOWPOS_CENTERED, SDL_WINDOWPOS_CENTERED,
			this.getWidth(), this.getHeight(),
			SDL_WINDOW_SHOWN | SDL_WINDOW_RESIZABLE
		);
		
		if (this.window == null) {
			throw new IllegalStateException("SDL Initialization Error: " + SDL_GetError());
		}
		
		this.renderer = SDL_CreateRenderer(this.window, -1, SDL_RENDERER_ACCELERATED);
		
		if (this.renderer == null) {
			throw new IllegalStateException("SDL Initialization Error: " + SDL_GetError());
		}
		
		SDL_SetRenderDrawBlendMode(this.renderer, SDL_BLENDMODE_BLEND);
		
		this.running = true;
	}
	
	public int getWidth() {
		return this.width;
	}
	
	public int getHeight() {
		return this.height;
	}
	
	public boolean isRunning() {
		return this.running;
	}
	
	public void setDrawColor(Color color) {
		SDL_SetRenderDrawColor(this.renderer, (byte) color.r, (byte) color.g, (byte) color.b, (byte) color.a);
	}
	
	public void clear() {
        SDL_RenderClear(this.renderer);
	}
	
	public void drawLine(int x1, int y1, int x2, int y2) {
		SDL_RenderDrawLine(this.renderer, x1, y1, x2, y2);
	}
	
	public void drawThickLine(int x1, int y1, int x2, int y2) {
		SDL_RenderDrawLine(this.renderer, x1, y1+1, x2, y2+1);
		SDL_RenderDrawLine(this.renderer, x1, y1-1, x2, y2-1);
		SDL_RenderDrawLine(this.renderer, x1, y1, x2, y2);
	}
	
	public void drawRectangle(int x, int y, int w, int h) {
		SDL_Rect r = new SDL_Rect();
		r.x = x;
		r.y = y;
		r.w = w;
		r.h = h;
		
		SDL_RenderFillRect(this.renderer, r);
	}
	
	public void drawTriangle(Triangle tri, Color triColor, boolean drawMesh) {
		ArrayList<SDL_Vertex> sdlVertices = new ArrayList<>();
		ArrayList<Vector> screenVertices = new ArrayList<>();
		
		for (Vector v : tri.getVertices()) {

			SDL_FPoint fpoint = new SDL_FPoint((int) (this.getWidth() * v.comp(0)), (int) (this.getHeight() * v.comp(1)));
			
			screenVertices.add(new Vector(new double[] {
				(int) (this.getWidth() * v.comp(0)), (int) (this.getHeight() * v.comp(1))
			}));
			
			SDL_Color color = new SDL_Color(
				triColor.r,
				triColor.g,
				triColor.b,
				triColor.a
			);			
			
			SDL_Vertex vert = new SDL_Vertex(fpoint, color, null);
			
			sdlVertices.add(vert);
		}
		
		SDL_RenderGeometry(this.renderer, null, sdlVertices, null);
		
		Vector a = screenVertices.get(0);
		Vector b = screenVertices.get(1);
		Vector c = screenVertices.get(2);
		
		if (drawMesh) {
			this.setDrawColor(new Color(0x00, 0x00, 0x00, 0x7f));
			
			this.drawLine((int) a.comp(0), (int) a.comp(1), (int) b.comp(0), (int) b.comp(1));
			this.drawLine((int) b.comp(0), (int) b.comp(1), (int) c.comp(0), (int) c.comp(1));
			this.drawLine((int) c.comp(0), (int) c.comp(1), (int) a.comp(0), (int) a.comp(1));
		}
	}
	
	public void present() {
        SDL_RenderPresent(this.renderer);
	}
	
	public void delay(int ms) {
		SDL_Delay(ms);
	}
	
	public ArrayList<Event> getEvents() {
		SDL_Event e = new SDL_Event();
		ArrayList<Event> events = new ArrayList<>();
		
		while (SDL_PollEvent(e) != 0) {
			switch (e.type) {
			case SDL_QUIT:
				this.running = false;
				
				SDL_Quit();
				break;
			case SDL_WINDOWEVENT:
				if (e.window.event == SDL_WINDOWEVENT_RESIZED) {
					this.width = e.window.data1;
					this.height = e.window.data2;
				}
			case SDL_KEYDOWN:
				// This actually accounts for key repeat so you don't really have to keep track of like each key in a map or anything like that.
				String downName = SDL_GetKeyName(e.key.keysym.sym);
				events.add(new KeyEvent(KeyEvent.PRESS, downName));
				break;
			case SDL_KEYUP:
				String upName = SDL_GetKeyName(e.key.keysym.sym);
				events.add(new KeyEvent(KeyEvent.RELEASE, upName));
				break;
			case SDL_MOUSEMOTION:
				MouseEvent me = new MouseEvent(MouseEvent.MOVE, e.motion.x, e.motion.y);
				me.dx = e.motion.xrel;
				me.dy = e.motion.yrel;
				events.add(me);
				break;
			// Actually I might not need this event but I'll keep it for now
			case SDL_MOUSEBUTTONDOWN:
				events.add(new MouseEvent(MouseEvent.LEFTCLICK, e.button.x, e.button.y));
				break;
			}
		}
		
		return events;
	}
}
