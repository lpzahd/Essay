package com.lpzahd.gallery.a3d;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.view.MotionEvent;

import com.lpzahd.gallery.R;
import com.lpzahd.gallery.a3d.PopupMenu.Option;
import com.lpzahd.gallery.waiter.MediaWaiter;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL11;

public final class HudLayer extends Layer {
    public static final int MODE_NORMAL = 0;
    public static final int MODE_SELECT = 1;

    private MediaWaiter mPresenter;
    private GridLayer mGridLayer;
    private final ImageButton mTopRightButton = new ImageButton();
    private final ImageButton mZoomInButton = new ImageButton();
    private final ImageButton mZoomOutButton = new ImageButton();
    private PathBarLayer mPathBar;
    private TimeBar mTimeBar;
    private MenuBar.Menu[] mNormalBottomMenu = null;
    private MenuBar.Menu[] mSingleViewIntentBottomMenu = null;
    private final MenuBar mSelectionMenuBottom;
    private final MenuBar mSelectionMenuTop;
    private final MenuBar mFullscreenMenu;
    private final LoadingLayer mLoadingLayer = new LoadingLayer();
    private RenderView mView = null;

    private int mMode = MODE_NORMAL;

    // Camera button - launches the camera intent when pressed.
    private static final int CAMERA_BUTTON_ICON = R.drawable.btn_camera;
    private static final int CAMERA_BUTTON_ICON_PRESSED = R.drawable.btn_camera_pressed;
    private static final int ZOOM_IN_ICON = R.drawable.btn_hud_zoom_in_normal;
    private static final int ZOOM_IN_ICON_PRESSED = R.drawable.btn_hud_zoom_in_pressed;
    private static final int ZOOM_OUT_ICON = R.drawable.btn_hud_zoom_out_normal;
    private static final int ZOOM_OUT_ICON_PRESSED = R.drawable.btn_hud_zoom_out_pressed;

    private final Runnable mCameraButtonAction = new Runnable() {
        public void run() {
            // Launch the camera intent.
            Intent intent = new Intent();
            intent.setClassName("com.android.camera", "com.android.camera.Camera");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mPresenter.getActivity().startActivity(intent);
        }
    };

    // Grid mode button - switches the media browser to grid mode.
    private static final int GRID_MODE_ICON = R.drawable.mode_stack;
    private static final int GRID_MODE_PRESSED_ICON = R.drawable.mode_stack;

    private final Runnable mZoomInButtonAction = new Runnable() {
        public void run() {
            mGridLayer.zoomInToSelectedItem();
            mGridLayer.markDirty(1);
        }
    };

    private final Runnable mZoomOutButtonAction = new Runnable() {
        public void run() {
            mGridLayer.zoomOutFromSelectedItem();
            mGridLayer.markDirty(1);
        }
    };

    private final Runnable mGridModeButtonAction = new Runnable() {
        public void run() {
            mGridLayer.setState(GridLayer.STATE_GRID_VIEW);
        }
    };

    /**
     * Stack mode button - switches the media browser to grid mode.
     */
    private static final int STACK_MODE_ICON = R.drawable.mode_grid;
    private static final int STACK_MODE_PRESSED_ICON = R.drawable.mode_grid;
    private final Runnable mStackModeButtonAction = new Runnable() {
        public void run() {
            mGridLayer.setState(GridLayer.STATE_TIMELINE);
        }
    };
    private float mAlpha;
    private float mAnimAlpha;
    private boolean mAutoHide;
    private float mTimeElapsedSinceFullOpacity;
    private String mCachedCaption;
    private String mCachedPosition;
    private String mCachedCurrentLabel;

    HudLayer(MediaWaiter mPresenter) {
        mAlpha = 1.0f;
        if (mTimeBar == null) {
            mTimeBar = new TimeBar(mPresenter.getActivity());
            mPathBar = new PathBarLayer();
        }
        mTopRightButton.setSize((int) (100 * MediaWaiter.PIXEL_DENSITY), (int) (94 * MediaWaiter.PIXEL_DENSITY));

        mZoomInButton.setSize(43 * MediaWaiter.PIXEL_DENSITY, 43 * MediaWaiter.PIXEL_DENSITY);
        mZoomOutButton.setSize(43 * MediaWaiter.PIXEL_DENSITY, 43 * MediaWaiter.PIXEL_DENSITY);
        mZoomInButton.setImages(ZOOM_IN_ICON, ZOOM_IN_ICON_PRESSED);
        mZoomInButton.setAction(mZoomInButtonAction);
        mZoomOutButton.setImages(ZOOM_OUT_ICON, ZOOM_OUT_ICON_PRESSED);
        mZoomOutButton.setAction(mZoomOutButtonAction);

        // The Share submenu is populated dynamically when opened.
        Resources resources = mPresenter.getActivity().getResources();
        Option[] deleteOptions = {
                new Option(mPresenter.getActivity().getResources().getString(R.string.confirm_delete), resources
                        .getDrawable(R.drawable.icon_delete), new Runnable() {
                    public void run() {
                        deleteSelection();
                    }
                }),
                new Option(mPresenter.getActivity().getResources().getString(R.string.cancel), resources
                        .getDrawable(R.drawable.icon_cancel), new Runnable() {
                    public void run() {

                    }
                }), };
        mSelectionMenuBottom = new MenuBar(mPresenter.getActivity());

        MenuBar.Menu shareMenu = new MenuBar.Menu.Builder(mPresenter.getActivity().getResources().getString(R.string.share)).icon(
                R.drawable.icon_share).onSelect(new Runnable() {
            public void run() {
                updateShareMenu();
            }
        }).build();

        MenuBar.Menu deleteMenu = new MenuBar.Menu.Builder(mPresenter.getActivity().getResources().getString(R.string.delete)).icon(
                R.drawable.icon_delete).options(deleteOptions).build();

        MenuBar.Menu moreMenu = new MenuBar.Menu.Builder(mPresenter.getActivity().getResources().getString(R.string.more))
                .icon(R.drawable.icon_more).onSelect(new Runnable() {
                    public void run() {
                        buildMoreOptions();
                    }
                }).build();

        mNormalBottomMenu = new MenuBar.Menu[] { shareMenu, deleteMenu, moreMenu };
        mSingleViewIntentBottomMenu = new MenuBar.Menu[] { shareMenu, moreMenu };

        mSelectionMenuBottom.setMenus(mNormalBottomMenu);
        mSelectionMenuTop = new MenuBar(mPresenter.getActivity());
        mSelectionMenuTop.setMenus(new MenuBar.Menu[] {
                new MenuBar.Menu.Builder(mPresenter.getActivity().getResources().getString(R.string.select_all)).onSelect(new Runnable() {
                    public void run() {
                        mGridLayer.selectAll();
                    }
                }).build(), new MenuBar.Menu.Builder("").build(),
                new MenuBar.Menu.Builder(mPresenter.getActivity().getResources().getString(R.string.deselect_all)).onSelect(new Runnable() {
                    public void run() {
                        mGridLayer.deselectOrCancelSelectMode();
                    }
                }).build() });
        mFullscreenMenu = new MenuBar(mPresenter.getActivity());
        mFullscreenMenu.setMenus(new MenuBar.Menu[] {
                new MenuBar.Menu.Builder(mPresenter.getActivity().getResources().getString(R.string.slideshow)).icon(R.drawable.icon_play)
                        .onSingleTapUp(new Runnable() {
                            public void run() {
                                if (getAlpha() == 1.0f)
                                    mGridLayer.startSlideshow();
                                else
                                    setAlpha(1.0f);
                            }
                        }).build(), /* new MenuBar.Menu.Builder("").build(), */
                new MenuBar.Menu.Builder(mPresenter.getActivity().getResources().getString(R.string.menu)).icon(R.drawable.icon_more).onSingleTapUp(
                        new Runnable() {
                            public void run() {
                                if (getAlpha() == 1.0f)
                                    mGridLayer.enterSelectionMode();
                                else
                                    setAlpha(1.0f);
                            }
                        }).build() });
    }

    public void setContext(MediaWaiter presenter) {
        if (mPresenter != presenter) {
            mPresenter = presenter;
            mTimeBar.regenerateStringsForContext(mPresenter.getActivity());
        }
    }

    private void buildMoreOptions() {
        ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();

        int numBuckets = buckets.size();
        boolean albumMode = false;
        boolean singleItem = false;
        boolean isPicasa = false;
        int mediaType = MediaItem.MEDIA_TYPE_IMAGE;
        if (numBuckets > 1) {
            albumMode = true;
        }
        if (numBuckets == 1) {
            MediaBucket bucket = buckets.get(0);
            MediaSet mediaSet = bucket.mediaSet;
            if (mediaSet == null) {
                return;
            }
            isPicasa = mediaSet.mPicasaAlbumId != Shared.INVALID;
            if (bucket.mediaItems == null || bucket.mediaItems.size() == 0) {
                albumMode = true;
            } else {
                ArrayList<MediaItem> items = bucket.mediaItems;
                int numItems = items.size();
                mediaType = items.get(0).getMediaType();
                if (numItems == 1) {
                    singleItem = true;
                } else {
                    for (int i = 1; i < numItems; ++i) {
                        if (items.get(0).getMediaType() != mediaType) {
                            albumMode = true;
                            break;
                        }
                    }
                }
            }
        }

        Option[] optionAll = new Option[] { new Option(mPresenter.getActivity().getResources().getString(R.string.details), mPresenter.getActivity()
                .getResources().getDrawable(R.drawable.ic_menu_view_details), new Runnable() {
            public void run() {
                ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                final AlertDialog.Builder builder = new AlertDialog.Builder(mPresenter.getActivity());
                builder.setTitle(mPresenter.getActivity().getResources().getString(R.string.details));
                boolean foundDataToDisplay = true;

                if (buckets == null) {
                    foundDataToDisplay = false;
                } else {
                    CharSequence[] strings = DetailMode.populateDetailModeStrings(mPresenter, buckets);
                    if (strings == null) {
                        foundDataToDisplay = false;
                    } else {
                        builder.setItems(strings, null);
                    }
                }

                mGridLayer.deselectAll();
                if (foundDataToDisplay) {
                    builder.setNeutralButton(R.string.details_ok, null);
                    mPresenter.getActivity().getHandler().post(new Runnable() {
                        public void run() {
                            builder.show();
                        }
                    });
                }
            }
        }) };

        Option[] optionSingle = new Option[] { new Option(mPresenter.getActivity().getResources().getString(R.string.show_on_map),
                mPresenter.getActivity().getResources().getDrawable(R.drawable.ic_menu_mapmode), new Runnable() {
                    public void run() {
                        ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                        MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                        if (item == null) {
                            return;
                        }
                        mGridLayer.deselectAll();
                        Util.openMaps(mPresenter.getActivity(), item.mLatitude, item.mLongitude);
                    }
                }), };

        Option[] optionImageMultiple = new Option[] {
                new Option(mPresenter.getActivity().getResources().getString(R.string.rotate_left), mPresenter.getActivity().getResources().getDrawable(
                        R.drawable.ic_menu_rotate_left), new Runnable() {
                    public void run() {
                        mGridLayer.rotateSelectedItems(-90.0f);
                    }
                }),
                new Option(mPresenter.getActivity().getResources().getString(R.string.rotate_right), mPresenter.getActivity().getResources().getDrawable(
                        R.drawable.ic_menu_rotate_right), new Runnable() {
                    public void run() {
                        mGridLayer.rotateSelectedItems(90.0f);
                    }
                }), };

        if (isPicasa) {
            optionImageMultiple = new Option[] {};
        }
        Option[] optionImageSingle;
        if (isPicasa) {
            optionImageSingle = new Option[] { new Option(mPresenter.getActivity().getResources().getString(R.string.set_as_wallpaper),
                    mPresenter.getActivity().getResources().getDrawable(R.drawable.ic_menu_set_as), new Runnable() {
                        public void run() {
                            ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                            MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                            if (item == null) {
                                return;
                            }
                            mGridLayer.deselectAll();
                            if (item.mParentMediaSet.mPicasaAlbumId != Shared.INVALID) {
                                final Intent intent = new Intent("android.intent.action.ATTACH_DATA");
                                intent.setClassName("com.cooliris.media", "com.cooliris.media.Photographs");
                                intent.setData(Uri.parse(item.mContentUri));
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                mPresenter.getActivity().startActivityForResult(intent, 0);
                            }
                        }
                    }) };
        } else {
            optionImageSingle = new Option[] {
                    new Option((isPicasa) ? mPresenter.getActivity().getResources().getString(R.string.set_as_wallpaper) : mPresenter.getActivity()
                            .getResources().getString(R.string.set_as), mPresenter.getActivity().getResources().getDrawable(
                            R.drawable.ic_menu_set_as), new Runnable() {
                        public void run() {
                            ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                            MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                            if (item == null) {
                                return;
                            }
                            mGridLayer.deselectAll();
                            if (item.mParentMediaSet.mPicasaAlbumId != Shared.INVALID) {
                                final Intent intent = new Intent("android.intent.action.ATTACH_DATA");
                                intent.setClassName("com.cooliris.media", "com.cooliris.media.Photographs");
                                intent.setData(Uri.parse(item.mContentUri));
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                mPresenter.getActivity().startActivityForResult(intent, 0);
                            } else {
                                Intent intent = Util.createSetAsIntent(Uri.parse(item.mContentUri), item.mMimeType);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                mPresenter.getActivity().startActivity(Intent.createChooser(intent, mPresenter.getActivity()
                                        .getText(R.string.set_image)));
                            }
                        }
                    }),
                    new Option(mPresenter.getActivity().getResources().getString(R.string.crop), mPresenter.getActivity().getResources().getDrawable(
                            R.drawable.ic_menu_crop), new Runnable() {
                        public void run() {
                            ArrayList<MediaBucket> buckets = mGridLayer.getSelectedBuckets();
                            MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                            if (item == null) {
                                return;
                            }
                            mGridLayer.deselectAll();
                            final Intent intent = new Intent("com.android.camera.action.CROP");
                            intent.setClassName("com.cooliris.media", "com.cooliris.media.CropImage");
                            intent.setData(Uri.parse(item.mContentUri));
                            mPresenter.getActivity().startActivityForResult(intent, MediaWaiter.CROP_MSG_INTERNAL);
                        }
                    }) };
        }
        Option[] options = optionAll;
        if (!albumMode) {
            if (!singleItem) {
                if (mediaType == MediaItem.MEDIA_TYPE_IMAGE)
                    options = concat(options, optionImageMultiple);
            } else {
                MediaItem item = MediaBucketList.getFirstItemSelection(buckets);
                if (item.mLatitude != 0.0f && item.mLongitude != 0.0f) {
                    options = concat(options, optionSingle);
                }
                if (mediaType == MediaItem.MEDIA_TYPE_IMAGE) {
                    options = concat(options, optionImageSingle);
                    options = concat(options, optionImageMultiple);
                }
            }
        }

        // We are assuming that the more menu is the last item in the menu
        // array.
        int lastIndex = mSelectionMenuBottom.getMenus().length - 1;
        mSelectionMenuBottom.getMenus()[lastIndex].options = options;
    }

    private static Option[] concat(Option[] A, Option[] B) {
        Option[] C = (Option[]) new Option[A.length + B.length];
        System.arraycopy(A, 0, C, 0, A.length);
        System.arraycopy(B, 0, C, A.length, B.length);
        return C;
    }

    public void updateNumItemsSelected(int numItems) {
        String items = " " + ((numItems == 1) ? mPresenter.getActivity().getString(R.string.item) : mPresenter.getActivity().getString(R.string.items));
        MenuBar.Menu menu = new MenuBar.Menu.Builder(numItems + items).config(MenuBar.MENU_TITLE_STYLE_TEXT).build();
        mSelectionMenuTop.updateMenu(menu, 1);
    }

    protected void deleteSelection() {
        mGridLayer.deleteSelection();
    }

    void setGridLayer(GridLayer layer) {
        mGridLayer = layer;
        updateViews();
    }

    int getMode() {
        return mMode;
    }

    void setMode(int mode) {
        if (mMode != mode) {
            mMode = mode;
            updateViews();
        }
    }

    @Override
    protected void onSizeChanged() {
        final float width = mWidth;
        final float height = mHeight;
        closeSelectionMenu();

        mTimeBar.setPosition(0f, height - TimeBar.HEIGHT * MediaWaiter.PIXEL_DENSITY);
        mTimeBar.setSize(width, TimeBar.HEIGHT * MediaWaiter.PIXEL_DENSITY);
        mSelectionMenuTop.setPosition(0f, 0);
        mSelectionMenuTop.setSize(width, MenuBar.HEIGHT * MediaWaiter.PIXEL_DENSITY);
        mSelectionMenuBottom.setPosition(0f, height - MenuBar.HEIGHT * MediaWaiter.PIXEL_DENSITY);
        mSelectionMenuBottom.setSize(width, MenuBar.HEIGHT * MediaWaiter.PIXEL_DENSITY);

        mFullscreenMenu.setPosition(0f, height - MenuBar.HEIGHT * MediaWaiter.PIXEL_DENSITY);
        mFullscreenMenu.setSize(width, MenuBar.HEIGHT * MediaWaiter.PIXEL_DENSITY);

        mPathBar.setPosition(0f, -4f * MediaWaiter.PIXEL_DENSITY);
        computeSizeForPathbar();

        mTopRightButton.setPosition(width - mTopRightButton.getWidth(), 0f);
        mZoomInButton.setPosition(width - mZoomInButton.getWidth(), 0f);
        mZoomOutButton.setPosition(width - mZoomInButton.getWidth(), mZoomInButton.getHeight());
    }

    private void computeSizeForPathbar() {
        float pathBarWidth = mWidth
                - ((mGridLayer.getState() == GridLayer.STATE_FULL_SCREEN) ? 32 * MediaWaiter.PIXEL_DENSITY
                        : 120 * MediaWaiter.PIXEL_DENSITY);
        mPathBar.setSize(pathBarWidth, (float) Math.ceil(39 * MediaWaiter.PIXEL_DENSITY));
        mPathBar.recomputeComponents();
    }

    public void setFeed(MediaFeed feed, int state, boolean needsLayout) {
        mTimeBar.setFeed(feed, state, needsLayout);
    }

    public void onGridStateChanged() {
        updateViews();
    }

    private void updateViews() {
        if (mGridLayer == null)
            return;
        final int state = mGridLayer.getState();
        // Show the selection menu in selection mode.
        final boolean selectionMode = mMode == MODE_SELECT;
        final boolean fullscreenMode = state == GridLayer.STATE_FULL_SCREEN;
        final boolean stackMode = state == GridLayer.STATE_MEDIA_SETS || state == GridLayer.STATE_TIMELINE;
        mSelectionMenuTop.setHidden(!selectionMode || fullscreenMode);
        mSelectionMenuBottom.setHidden(!selectionMode);
        mFullscreenMenu.setHidden(!fullscreenMode || selectionMode);
        mZoomInButton.setHidden(mFullscreenMenu.isHidden());
        mZoomOutButton.setHidden(mFullscreenMenu.isHidden());

        // Show the time bar in stack and grid states, except in selection mode.
        mTimeBar.setHidden(fullscreenMode || selectionMode || stackMode);
        // mTimeBar.setHidden(selectionMode || (state !=
        // GridLayer.STATE_TIMELINE && state != GridLayer.STATE_GRID_VIEW));

        // Hide the path bar and top-right button in selection mode.
        mPathBar.setHidden(selectionMode);
        mTopRightButton.setHidden(selectionMode || fullscreenMode);
        computeSizeForPathbar();

        // Configure the top-right button.
        int image = 0;
        int pressedImage = 0;
        Runnable action = null;
        final ImageButton topRightButton = mTopRightButton;
        int height = (int) (94 * MediaWaiter.PIXEL_DENSITY);
        switch (state) {
        case GridLayer.STATE_MEDIA_SETS:
            image = CAMERA_BUTTON_ICON;
            pressedImage = CAMERA_BUTTON_ICON_PRESSED;
            action = mCameraButtonAction;
            break;
        case GridLayer.STATE_GRID_VIEW:
            height /= 2;
            image = STACK_MODE_ICON;
            pressedImage = STACK_MODE_PRESSED_ICON;
            action = mStackModeButtonAction;
            break;
        case GridLayer.STATE_TIMELINE:
            image = GRID_MODE_ICON;
            pressedImage = GRID_MODE_PRESSED_ICON;
            action = mGridModeButtonAction;
            break;
        default:
            break;
        }
        topRightButton.setSize((int) (100 * MediaWaiter.PIXEL_DENSITY), height);
        topRightButton.setImages(image, pressedImage);
        topRightButton.setAction(action);
    }

    public TimeBar getTimeBar() {
        return mTimeBar;
    }

    public PathBarLayer getPathBar() {
        return mPathBar;
    }

    public GridLayer getGridLayer() {
        return mGridLayer;
    }

    @Override
    public boolean update(RenderView view, float frameInterval) {
        float factor = 1.0f;
        if (mAlpha == 1.0f) {
            // Speed up the animation when it becomes visible.
            factor = 4.0f;
        }
        mAnimAlpha = FloatUtils.animate(mAnimAlpha, mAlpha, frameInterval * factor);
        boolean timeElapsedSinceFullOpacity_Reset = mTimeElapsedSinceFullOpacity == 0.0f;

        if (mAutoHide) {
            if (mAlpha == 1.0f && mMode != MODE_SELECT) {
                mTimeElapsedSinceFullOpacity += frameInterval;
                if (mTimeElapsedSinceFullOpacity > 5.0f)
                    setAlpha(0);
            }
        }
        if (mAnimAlpha != mAlpha || (mTimeElapsedSinceFullOpacity < 5.0f && !timeElapsedSinceFullOpacity_Reset))
            return true;

        return false;
    }

    public void renderOpaque(RenderView view, GL11 gl) {

    }

    public void renderBlended(RenderView view, GL11 gl) {
        view.setAlpha(mAnimAlpha);
    }

    public void setAlpha(float alpha) {
        float oldAlpha = mAlpha;
        mAlpha = alpha;
        if (oldAlpha != alpha) {
            if (mView != null)
                mView.requestRender();
        }
        if (alpha == 1.0f) {
            mTimeElapsedSinceFullOpacity = 0.0f;
        }
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setTimeBarTime(long time) {
        // mTimeBar.setTime(time);
    }

    @Override
    public void generate(RenderView view, RenderView.Lists lists) {
        lists.opaqueList.add(this);
        lists.blendedList.add(this);
        lists.hitTestList.add(this);
        lists.updateList.add(this);
        mTopRightButton.generate(view, lists);
        mZoomInButton.generate(view, lists);
        mZoomOutButton.generate(view, lists);
        mTimeBar.generate(view, lists);
        mSelectionMenuTop.generate(view, lists);
        mSelectionMenuBottom.generate(view, lists);
        mFullscreenMenu.generate(view, lists);
        mPathBar.generate(view, lists);
        // mLoadingLayer.generate(view, lists);
        mView = view;
    }

    @Override
    public boolean containsPoint(float x, float y) {
        return false;
    }

    public void cancelSelection() {
        mSelectionMenuBottom.close();
        closeSelectionMenu();
        setMode(MODE_NORMAL);
    }

    public void closeSelectionMenu() {
        mSelectionMenuBottom.close();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mMode == MODE_SELECT) {
            /*
             * setMode(MODE_NORMAL); ArrayList<MediaBucket> displayBuckets =
             * mGridLayer.getSelectedBuckets(); // use this list, and then clear
             * the items return true;
             */
            return false;
        } else {
            return false;
        }
    }

    public boolean isLoaded() {
        return mLoadingLayer.isLoaded();
    }

    void reset() {
        mLoadingLayer.reset();
        mTimeBar.regenerateStringsForContext(mPresenter.getActivity());
    }

    public void fullscreenSelectionChanged(MediaItem item, int index, int count) {
        // request = new ReverseGeocoder.Request();
        // request.firstLatitude = request.secondLatitude = item.latitude;
        // request.firstLongitude = request.secondLongitude = item.longitude;
        // mGeo.enqueue(request);
        if (item == null)
            return;
        String location = index + "/" + count;
        mCachedCaption = item.mCaption;
        mCachedPosition = location;
        mCachedCurrentLabel = location;
        mPathBar.changeLabel(location);
    }

    private void updateShareMenu() {
        // Get the first selected item. Wire this up to multiple-item intents
        // when we move
        // to Eclair.
        ArrayList<MediaBucket> selection = mGridLayer.getSelectedBuckets();
        ArrayList<Uri> uris = new ArrayList<Uri>();
        String mimeType = null;
        if (!selection.isEmpty()) {
            int mediaType = Shared.INVALID;
            int numBuckets = selection.size();
            for (int j = 0; j < numBuckets; ++j) {
                MediaBucket bucket = selection.get(j);
                ArrayList<MediaItem> items = null;
                int numItems = 0;
                if (bucket.mediaItems != null && !bucket.mediaItems.isEmpty()) {
                    items = bucket.mediaItems;
                    numItems = items.size();
                } else if (bucket.mediaSet != null) {
                    // We need to delete the entire bucket.
                    items = bucket.mediaSet.getItems();
                    numItems = bucket.mediaSet.getNumItems();
                }
                for (int i = 0; i < numItems; ++i) {
                    MediaItem item = items.get(i);
                    if (mimeType == null) {
                        mimeType = item.mMimeType;
                        mediaType = item.getMediaType();
                        MediaSet parentMediaSet = item.mParentMediaSet;
                        if (parentMediaSet != null && parentMediaSet.mPicasaAlbumId != Shared.INVALID) {
                            // This will go away once http uri's are supported
                            // for all media types.
                            // This ensures that just the link is shared as a
                            // text
                            mimeType = "text/plain";
                        }
                    }
                    if (mediaType == item.getMediaType()) {
                        // add this uri
                        if (item.mContentUri != null) {
                            Uri uri = Uri.parse(item.mContentUri);
                            uris.add(uri);
                        }
                    }
                }
            }
        }
        Option[] options = null;
        if (uris.size() != 0) {
            final Intent intent = new Intent();
            if (mimeType == null)
                mimeType = "image/jpeg";
            if (mimeType.contains("text")) {
                // We need to share this as a text string.
                intent.setAction(Intent.ACTION_SEND);
                intent.setType(mimeType);

                // Create a newline-separated list of URLs.
                StringBuilder builder = new StringBuilder();
                for (int i = 0, size = uris.size(); i < size; ++i) {
                    builder.append(uris.get(i));
                    if (i != size - 1) {
                        builder.append('\n');
                    }
                }
                intent.putExtra(Intent.EXTRA_TEXT, builder.toString());
            } else {
                if (uris.size() == 1) {
                    intent.setAction(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_STREAM, uris.get(0));
                } else {
                    intent.setAction(Intent.ACTION_SEND_MULTIPLE);
                    intent.putExtra(Intent.EXTRA_STREAM, uris);
                }
                intent.setType(mimeType);
            }
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Query the system for matching activities.
            PackageManager packageManager = mPresenter.getActivity().getPackageManager();
            List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0);
            int numActivities = activities.size();
            options = new Option[numActivities];
            for (int i = 0; i != numActivities; ++i) {
                final ResolveInfo info = activities.get(i);
                String label = info.loadLabel(packageManager).toString();
                options[i] = new Option(label, info.loadIcon(packageManager), new Runnable() {
                    public void run() {
                        startResolvedActivity(intent, info);
                    }
                });
            }
        }
        mSelectionMenuBottom.getMenus()[0].options = options;
    }

    private void startResolvedActivity(Intent intent, ResolveInfo info) {
        final Intent resolvedIntent = new Intent(intent);
        ActivityInfo ai = info.activityInfo;
        resolvedIntent.setComponent(new ComponentName(ai.applicationInfo.packageName, ai.name));
        mPresenter.getActivity().getHandler().post(new Runnable() {
            public void run() {
                mPresenter.getActivity().startActivity(resolvedIntent);
            }
        });
    }

    public void autoHide(boolean hide) {
        mAutoHide = hide;
    }

    public void swapFullscreenLabel() {
        mCachedCurrentLabel = (mCachedCurrentLabel == mCachedCaption || mCachedCaption == null) ? mCachedPosition : mCachedCaption;
        mPathBar.changeLabel(mCachedCurrentLabel);
    }

    public void clear() {

    }

    public void enterSelectionMode() {
        setAlpha(1.0f);
        setMode(HudLayer.MODE_SELECT);
        // if we are in single view mode, show the bottom menu without the
        // delete button.
        if (mGridLayer.noDeleteMode()) {
            mSelectionMenuBottom.setMenus(mSingleViewIntentBottomMenu);
        } else {
            mSelectionMenuBottom.setMenus(mNormalBottomMenu);
        }
    }

    public void computeBottomMenu() {
        // we need to the same for picasa albums
        ArrayList<MediaBucket> selection = mGridLayer.getSelectedBuckets();
        MenuBar.Menu[] menus = mSelectionMenuBottom.getMenus();
        if (menus == mSingleViewIntentBottomMenu)
            return;
        int numBuckets = selection.size();
        for (int i = 0; i < numBuckets; ++i) {
            MediaBucket bucket = selection.get(i);
            if (bucket.mediaSet.mPicasaAlbumId != Shared.INVALID) {
                mSelectionMenuBottom.setMenus(mSingleViewIntentBottomMenu);
                break;
            }
        }
    }

    public Layer getMenuBar() {
        return mFullscreenMenu;
    }

    public void hideZoomButtons(boolean hide) {
        mZoomInButton.setHidden(hide);
        mZoomOutButton.setHidden(hide);
    }
}
