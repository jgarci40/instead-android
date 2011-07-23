/* Include the SDL main definition header */
#include <SDL_main.h>

/*******************************************************************************
 Functions called by JNI
 *******************************************************************************/
#include <jni.h>
#include <unistd.h>

#include <android/log.h>

// Called before SDL_main() to initialize JNI bindings in SDL library
extern "C" void SDL_Android_Init(JNIEnv* env, jclass cls);

// Library init
extern "C" jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	return JNI_VERSION_1_4;
}

// Start up the SDL app
extern "C" void Java_com_silentlexx_instead_SDLActivity_nativeInit(
		JNIEnv* env, jclass cls, jstring jpath, jstring jres, jstring jgame) {

	/* This interface could expand with ABI negotiation, calbacks, etc. */
	SDL_Android_Init(env, cls);

	/* Run the application code! */
	int status;
	char *argv[6];
	int n = 1;
	if (jpath != NULL) {
		const char *path = env->GetStringUTFChars(jpath, 0);
		chdir(strdup(path));
		env->ReleaseStringUTFChars(jpath, path);
	}


	argv[0] = strdup("sdl-instead");

	if (jres != NULL) {
		const char *res = env->GetStringUTFChars(jres, 0);
		n = 3;
		argv[1] = strdup("-mode");
		argv[2] = strdup(res);
		env->ReleaseStringUTFChars(jres, res);

		if (jgame != NULL) {
			const char *game = env->GetStringUTFChars(jgame, 0);
			n = 5;
			argv[3] = strdup("-game");
			argv[4] = strdup(game);
			argv[5] = NULL;
			env->ReleaseStringUTFChars(jgame, game);
		} else {
			argv[3] = NULL;
		}

	} else {
		argv[1] = NULL;
	}
	//__android_log_print(ANDROID_LOG_DEBUG, "LEXX_Activity", "SDL param n %i", n);


	status = SDL_main(n, argv);




	int i;
	for (i = 0; i < n; ++i) {
		free(argv[i]);
	}

	//__android_log_print(ANDROID_LOG_DEBUG, "LEXX_Activity", "SDL Exit status %c", status);
	/* We exit here for consistency with other platforms. */
	exit(status);
}

