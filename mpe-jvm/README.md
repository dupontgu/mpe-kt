![Travis](https://travis-ci.org/dupontgu/mpe-kt.svg?branch=master)

### Add to your project

This will eventually make its way to jCenter, but JitPack works well enough for now.

in your root build.gradle file:
```groovy
allprojects {
    repositories {
		// other repos first...
		maven { url 'https://jitpack.io' }
	}
}
```

and add the dependency:
```groovy
dependencies {
    compile 'com.github.dupontgu:mpe-kt:master-SNAPSHOT'
}
```

Note that it's possible to pull any branch, release, or commit, just by changing the dependency above.  
Check out [JitPack](https://jitpack.io/#dupontgu/mpe-kt/) for details.
