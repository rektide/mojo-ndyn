module.exports= installNpm
function installNpm(pkgs){
	var npm= require("npm")
	npm.load(function(err, npm){
		npm.install(pkgs)
	})	
}
