# 🚀 GitHub Actions Quick Start Guide

## What I've Set Up

✅ Created `.github/workflows/build.yml` - Automated build workflow  
✅ Created `.gitignore` - Excludes build artifacts from Git  
✅ Initialized Git repository with all project files  
✅ Made initial commit (30 files committed)

---

## Steps to Build APK via GitHub Actions

### 1️⃣ Create a GitHub Repository

Go to https://github.com/new and create a new repository:
- **Repository name**: `ClawController` (or any name you prefer)
- **Visibility**: Public or Private (your choice)
- ❌ **DO NOT** initialize with README, .gitignore, or license (we already have them)
- Click **"Create repository"**

### 2️⃣ Push Your Code to GitHub

Open PowerShell in the project directory and run:

```powershell
cd C:\Users\asifs\.gemini\antigravity\scratch\ClawController

# Add the GitHub repository as remote (replace <YOUR_USERNAME> with your GitHub username)
git remote add origin https://github.com/<YOUR_USERNAME>/ClawController.git

# Push the code
git branch -M main
git push -u origin main
```

**Example:**
If your GitHub username is `asifsultanbhat`, the command would be:
```powershell
git remote add origin https://github.com/asifsultanbhat/ClawController.git
```

### 3️⃣ GitHub Actions Will Build Automatically

Once you push:
- GitHub Actions will automatically start building the APK
- Go to your repository on GitHub → **"Actions"** tab
- You'll see a workflow run called **"Build Android APK"**
- Wait ~5-10 minutes for the build to complete

### 4️⃣ Download the APK

After the build succeeds:
- Click on the completed workflow run
- Scroll down to **"Artifacts"** section
- Download **"ClawController-debug"** (it's a ZIP file)
- Extract the ZIP to get `app-debug.apk`

### 5️⃣ Install on Your Android Device

**Option A: Direct Transfer**
- Transfer `app-debug.apk` to your phone via USB or cloud storage
- Open the APK file on your phone
- Tap **"Install"** (you may need to enable "Install from Unknown Sources")

**Option B: ADB Install**
```bash
adb install app-debug.apk
```

---

## 🏷️ Creating Releases (Optional)

To automatically create GitHub Releases with the APK:

```powershell
# Tag your commit
git tag v1.0.0
git push origin v1.0.0
```

GitHub Actions will automatically:
- Build the APK
- Create a Release called **"v1.0.0"**
- Attach the APK to the release

Users can then download the APK directly from the **"Releases"** page.

---

## Troubleshooting

### "Authentication failed" when pushing
Run this first to authenticate:
```powershell
git config --global user.name "Your Name"
git config --global user.email "your-email@example.com"
```

Then use a Personal Access Token instead of password:
1. Go to GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic)
2. Generate new token with `repo` scope
3. Use the token as your password when pushing

### Build fails on GitHub Actions
- Check the **"Actions"** tab for error logs
- Most common issue: Gradle or dependency issues (usually auto-fixed by caching)
- Re-run the workflow if it was a transient error

### APK won't install on phone
- Make sure you downloaded from the **"Artifacts"** section (not the source code)
- Extract the ZIP file first
- Enable "Install from Unknown Sources" in your phone's settings

---

## Next Steps After Installation

Once the APK is installed:
1. Install **Termux**, **Termux:API**, and **Termux:Tasker** from F-Droid
2. Open ClawController
3. Configure your API keys in Settings
4. Run **Fresh Install** to set up OpenClaw
5. Tap **Start Runtime** to launch the gateway

Enjoy your automated OpenClaw setup! 🎉
