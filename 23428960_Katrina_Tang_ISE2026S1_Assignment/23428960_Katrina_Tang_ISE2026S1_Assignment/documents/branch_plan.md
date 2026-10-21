# Branch Plan

- main - main branch. It always contains clean, working code that could run.

- dev – development branch. All new features are combined here first before going to main.

- seasons-feature – A separate branch for building the season feature (Scenario A).
    
- temperature-feature – A separate branch for building the temperature feature (Scenario B).

# How to merge them:

  1. Create dev branch from main.

  2. Create seasons feature and temperature feature branches from dev.

  3. After finishing a feature, merge it into dev.

  4. After testing everything on dev and making sure it works, merge dev into main.
  
# Git Commands Used

```bash
# Repository setup
git init
git config --global user.name "Katrina Tang"
git config --global user.email "katrina.tang@student.curtin.edu.au"

# Initial commit
git add code/SeasonsClimate.java
git commit -m "Initial commit: basic module structure"

# Branch creation
git checkout -b dev main
git checkout -b seasons-feature dev
git checkout -b temperature-feature dev

# Feature work 
git checkout seasons-feature
git add code/SeasonsClimate.java
git commit -m "Implemented getMeteorologicalSeason module"

# Merging
git checkout dev
git merge seasons-feature
git merge temperature-feature

# Final merge to main
git checkout main
git merge dev

# View history
git log --oneline --graph
