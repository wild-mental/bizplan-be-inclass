#!/bin/bash

# Bizplan Backend Issues 일괄 생성 스크립트
# Frontend PoC (#001-#005)는 이미 완료되어 제외됨
# Backend 이슈만 생성 (#006-#015)

set -e

echo "================================================"
echo "Bizplan Backend Issues 생성 시작"
echo "================================================"
echo ""
echo "✅ Frontend PoC (#001-#005): 이미 완료됨 - 건너뜀"
echo "🔄 Backend Issues (#006-#015): 생성 중..."
echo ""

# GitHub CLI 설치 확인
if ! command -v gh &> /dev/null; then
    echo "❌ Error: GitHub CLI (gh) not installed"
    echo "Install: https://cli.github.com/"
    exit 1
fi

# GitHub 로그인 확인
if ! gh auth status &> /dev/null; then
    echo "❌ Error: Not logged in to GitHub"
    echo "Run: gh auth login"
    exit 1
fi

# 스크립트 실행 위치 확인
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Backend 이슈 생성
CREATED=0
FAILED=0

for i in {006..015}; do
    issue_file=$(ls issue-$i-*.md 2>/dev/null | head -1)
    
    if [ -f "$issue_file" ]; then
        issue_name=$(basename "$issue_file" .md)
        echo "[$((i-5))/10] Creating: $issue_name"
        
        if gh issue create -F "$issue_file" > /dev/null 2>&1; then
            echo "    ✅ Success"
            ((CREATED++))
        else
            echo "    ❌ Failed"
            ((FAILED++))
        fi
        
        # API rate limiting 방지
        sleep 2
    else
        echo "[$((i-5))/10] ⚠️  File not found: issue-$i-*.md"
        ((FAILED++))
    fi
done

echo ""
echo "================================================"
echo "Backend Issues 생성 완료"
echo "================================================"
echo "✅ 생성 성공: $CREATED 개"
if [ $FAILED -gt 0 ]; then
    echo "❌ 생성 실패: $FAILED 개"
fi
echo ""
echo "다음 단계:"
echo "1. GitHub에서 생성된 이슈 확인"
echo "2. 라벨 추가: gh issue edit <NUM> --add-label 'epic:...,type:...'"
echo "3. 마일스톤 설정: gh issue edit <NUM> --milestone 'Phase 2'"
echo "4. 프로젝트 보드 추가: gh project item-add <PROJECT_ID> --url <ISSUE_URL>"
echo ""
echo "자세한 내용: README.md 참조"
echo ""

