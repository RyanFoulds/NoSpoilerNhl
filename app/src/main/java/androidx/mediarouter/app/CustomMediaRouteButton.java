package androidx.mediarouter.app;

import android.content.Context;
import android.util.AttributeSet;

import androidx.mediarouter.media.MediaRouter;

public class CustomMediaRouteButton extends MediaRouteButton
{
    private MediaRouter mediaRouter;

    public CustomMediaRouteButton(final Context context)
    {
        super(context);
        init();
    }

    public CustomMediaRouteButton(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public CustomMediaRouteButton(final Context context,
                                  final AttributeSet attrs,
                                  final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init()
    {
        if (!isInEditMode())
        {
            mediaRouter = MediaRouter.getInstance(getContext());
        }
    }

    @Override
    public void setEnabled(final boolean enabled)
    {
        super.setEnabled(enabled);
        refreshVisibility();
    }

    @Override
    public void refreshVisibility()
    {
        if (!mediaRouter.isRouteAvailable(super.getRouteSelector(), MediaRouter.AVAILABILITY_FLAG_IGNORE_DEFAULT_ROUTE))
        {
            setAlpha(0f);
            setClickable(false);
        }
        else
        {
            super.setAlpha(1f);
            setClickable(true);
            super.refreshVisibility();
        }
    }

}
