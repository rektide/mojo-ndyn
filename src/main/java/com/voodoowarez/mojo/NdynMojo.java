package com.voodoowarez.mojo;

import java.io.File;
import java.io.StringWriter;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonWriter;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.dynjs.runtime.DynJS;
import org.dynjs.runtime.DynObject;
import org.dynjs.runtime.GlobalObject;
import org.projectodd.nodyn.buffer.BufferType;
import org.projectodd.nodyn.modules.NpmModuleProvider;

/**
* Goal which fetches from npm
*/
@Mojo(name= "ndyn", defaultPhase= LifecyclePhase.GENERATE_SOURCES)
public class NdynMojo extends AbstractMojo
{
	/**
	* Location to fetch into
	*/
	@Parameter(defaultValue= "${project.build.directory}")
	protected File outputDirectory;

	/**
	* Install stings to be fetched
	*/
	@Parameter(required= true)
	protected String[] install;

	public void execute() throws MojoExecutionException{
		final DynJS runtime = loadRuntime();

		// load installScript from file
		/*
		String installScript;
		final URL installScriptUrl = this.getClass().getResource("/npmInstall.js");
		try{
			
			final File installScriptFile = new File(installScriptUrl.toURI());
			installScript = Files.toString(installScriptFile, Charset.forName("UTF-8"));
		}catch (URISyntaxException ex){
			throw new MojoExecutionException("Couldn't find the install script", ex);
		}catch(IOException ex){
			throw new MojoExecutionException("Failed file read", ex);
		}
		*/

		// run
		runtime.evaluate("require('installScript.js')("+toJson(install)+")");
	}

	protected DynJS loadRuntime(){
		final DynJS runtime = new DynJS();
		final GlobalObject globalObject = runtime.getExecutionContext().getGlobalObject();

		final BufferType bufferType = new BufferType(globalObject);
		final DynObject node = new DynObject(globalObject);
		node.put("buffer", bufferType);

		//node.put("QueryString", new QueryString(globalObject));

		globalObject.defineGlobalProperty("nodyn", node);
		globalObject.defineGlobalProperty("global", globalObject);
		globalObject.defineGlobalProperty("__filename", "repl");
		new NpmModuleProvider(globalObject);

		return runtime;
	}

	protected String toJson(String[] els){
		final JsonArrayBuilder args = Json.createArrayBuilder();
		for(String el : els){
			args.add(el);
		}

		final StringWriter stWriter = new StringWriter();
		final JsonWriter jsonWriter = Json.createWriter(stWriter);
		jsonWriter.writeArray(args.build());
		jsonWriter.close();
		return stWriter.toString();
	}
}
