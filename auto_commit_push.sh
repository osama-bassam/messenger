#!/bin/bash
git add .  # Stage all changes
git commit -m "Auto commit $(date)"  # Commit with the current date as the message
git push origin main  # Push to GitHub (use your branch name)
