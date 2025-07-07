# Remove all generated files from git tracking
cd /home/siddharth/Projects/DevOps/team-oopsops

# Remove generated files
rm -rf server/*/generated/
rm -rf server/*/generated-client/
rm -rf genai-service/generated/
rm -rf client/src/generated/
rm -rf docs/api.html

# Remove from git if already tracked
git rm -r --cached server/*/generated/ 2>/dev/null || true
git rm -r --cached server/*/generated-client/ 2>/dev/null || true
git rm -r --cached genai-service/generated/ 2>/dev/null || true
git rm -r --cached client/src/generated/ 2>/dev/null || true
git rm --cached docs/api.html 2>/dev/null || true