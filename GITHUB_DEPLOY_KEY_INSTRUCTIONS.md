# GitHub Deploy Key Setup Instructions

If you are experiencing "Permission Denied (publickey)" when trying to Export or Push to GitHub from AI Studio, follow these steps to add your deploy key to your repository:

## 1. Copy your Public Key
Open the `deploy_key.pub` file in the root of this project and copy its entire contents.

## 2. Add to GitHub
1.  Go to your repository on GitHub.
2.  Click on **Settings** in the top navigation bar.
3.  In the left sidebar, click on **Deploy keys** (under "Security").
4.  Click the **Add deploy key** button in the top right.
5.  **Title**: Give it a name like `AI Studio Build`.
6.  **Key**: Paste the public key you copied from `deploy_key.pub`.
7.  **Allow write access**: Check this box if you want AI Studio to be able to push changes to your repository.
8.  Click **Add key**.

## 3. Verify in AI Studio
Once the key is added to GitHub, try clicking the **Export / Push to GitHub** button again in the AI Studio header menu.

---

**Note**: If you are trying to set up a deployment to another service (like Netlify or Vercel) using this key, you should add it as a "Deploy Key" or "Secret" in that service's settings, following their specific documentation.
